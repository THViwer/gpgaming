package com.onepiece.treasure.games.live.golddeluxe

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.utils.RedisService
import com.onepiece.treasure.utils.StringUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class GoldDeluxeService(
        private val okHttpUtil: OkHttpUtil,
        private val redisService: RedisService
) : PlatformApi() {

    private val log = LoggerFactory.getLogger(GoldDeluxeService::class.java)

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    private val betDateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
    private val currencyCode = "MRY"

    private val url = "${GameConstant.GOLDDELUXE_API_URL}/release/www/merchantapi.php"


    private fun checkErrorCode(header: GoldDeluxeValue.Header) {
        check(header.errorCode == "0") {
            OnePieceExceptionCode.PLATFORM_METHOD_FAIL
        }
    }

    private fun generatorMessageId(first: String): String {
        return "$first${LocalDateTime.now().format(dateTimeFormat)}${StringUtil.generateNonce(5)}"
    }

    override fun register(registerReq: GameValue.RegisterReq): String {

        val messageId = this.generatorMessageId("M")
        val data = """
            <?xml version="1.0"?>
            <Request>
              <Header>
                <Method>cCreateMember</Method>
                <MerchantID>${(registerReq.token as DefaultClientToken).appId}</MerchantID>
                <MessageID>${messageId}</MessageID>
              </Header>
              <Param>
                <UserID>${registerReq.username}</UserID>
                <CurrencyCode>${currencyCode}</CurrencyCode>
                <BetGroup>default</BetGroup>
              </Param>
            </Request>
        """.trimIndent()

        val result = okHttpUtil.doPostXml(url, data, GoldDeluxeValue.RegisterResult::class.java)
        this.checkErrorCode(result.header)

        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val token = balanceReq.token as DefaultClientToken
        val messageId = this.generatorMessageId("C")
        val data = """
            
            <?xml version="1.0"?>
            <Request>
              <Header>
                <Method>cCheckClient</Method>
                <MerchantID>${token.appId}</MerchantID>
                <MessageID>${messageId}</MessageID>
              </Header>
              <Param>
                <UserID>${balanceReq.username}</UserID>
                <CurrencyCode>${currencyCode}</CurrencyCode>
                <RequestBetLimit>0</RequestBetLimit>
              </Param>
            </Request>
        """.trimIndent()

        val result = okHttpUtil.doPostXml(url, data, GoldDeluxeValue.BalanceResult::class.java)
        this.checkErrorCode(result.header)

        return result.param.balance
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        val token = transferReq.token as DefaultClientToken

        val data = when (transferReq.amount.toDouble() > 0) {
            true -> {
                val messageId = this.generatorMessageId("W")
                """
                    <?xml version="1.0"?>
                    <Request>
                      <Header>
                        <Method>cWithdrawal</Method>
                        <MerchantID>${token.appId}</MerchantID>
                        <MessageID>${messageId}</MessageID>
                      </Header>
                      <Param>
                        <UserID>${transferReq.username}</UserID>
                        <CurrencyCode>${currencyCode}</CurrencyCode>
                        <Amount>${transferReq.amount}</Amount>
                        <EnableInGameTransfer>1</EnableInGameTransfer>
                        <GetEndBalance>1</GetEndBalance>
                      </Param>
                    </Request>
                """.trimIndent()
            }
            false -> {
                val messageId = this.generatorMessageId("D")
                """
                    <?xml version="1.0"?>
                    <Request>
                      <Header>
                        <Method>cDeposit</Method>
                        <MerchantID>${token.appId}</MerchantID>
                        <MessageID>${messageId}</MessageID>
                      </Header>
                      <Param>
                        <UserID>${transferReq.username}</UserID>
                        <CurrencyCode>${currencyCode}</CurrencyCode>
                        <Amount>${transferReq.amount.negate()}</Amount>
                        <EnableInGameTransfer>1</EnableInGameTransfer>
                        <GetEndBalance>1</GetEndBalance>
                      </Param>
                    </Request>
                """.trimIndent()
            }
        }

        val result = okHttpUtil.doPostXml(url, data, GoldDeluxeValue.TransferResult::class.java)
        this.checkErrorCode(result.header)
        check(result.param.errorDesc.isBlank()) { OnePieceExceptionCode.PLATFORM_METHOD_FAIL }

        return result.param.paymentId
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val token = checkTransferReq.token as DefaultClientToken
        val messageId = this.generatorMessageId("S")

        val data = """
            <?xml version=”1.0”?>
            <Request>
              <Header>
                <Method>cCheckTransactionStatus</Method>
                <MerchantID>${token.appId}</MerchantID>
                <MessageID>${messageId}</MessageID>
              </Header>
              <Param>
                <MessageID>${checkTransferReq.orderId}</MessageID>
                <UserID>${checkTransferReq.username}</UserID>
                <CurrencyCode>${currencyCode}</CurrencyCode>
                </Param>
              </Request>
        """.trimIndent()

        val result = okHttpUtil.doPostXml(url, data, GoldDeluxeValue.CheckTransferResult::class.java)
        this.checkErrorCode(result.header)

        return result.param.status == "SUCCESS"
    }


    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): Any {
        val token = betOrderReq.token as DefaultClientToken
        val messageId = this.generatorMessageId("H")

        val data = """
            <?xml version="1.0"?>
            <Request>
              <Header>
                <Method>cGetBetHistory</Method>
                <MerchantID>${token.appId}</MerchantID>
                <MessageID>${messageId}</MessageID>
              </Header>
              <Param>
                <FromTime>${betOrderReq.startTime.format(betDateTimeFormat)}</FromTime>
                <ToTime>${betOrderReq.endTime.format(betDateTimeFormat)}</ToTime>
                <Index></Index>
                <UserID>${betOrderReq.username}</UserID >
                <ShowBalance>1</ShowBalance>
                <SearchByBalanceTime>1</SearchByBalanceTime>
                <ShowRefID>1</ShowRefID>
                <ShowOdds>1</ShowOdds>
              </Param>
            </Request>
        """.trimIndent()



        return ""


    }

    override fun asynBetOrder(syncBetOrderReq: GameValue.SyncBetOrderReq): String {
        val token = syncBetOrderReq.token as DefaultClientToken
        val messageId = this.generatorMessageId("H")


        val nextId = redisService.get(OnePieceRedisKeyConstant.jokerNextId(), String::class.java) {
            ""
        }!!

        return ""
    }



    // 支持的语言
    /**
     * zh-cn Chinese (PRC) 中文（中國）
     * en	English英語
     * ja	Japanese 日文
     * ko	Korean韓語
     * id	Indonesian印度尼西亞語
     * th	Thai泰語
     * vi	Vietnamese 越南語
     */
    override fun start(startReq: GameValue.StartReq): String {

        val token = startReq.token as DefaultClientToken
        val signParam = "${token.appId}${token.key}${startReq.username}${currencyCode}"
        val sign = ""

        val lang = when (startReq.language) {
            Language.CN -> "zh-cn"
            Language.EN -> "en"
            Language.TH -> "th"
            Language.ID -> "id"
            Language.VI -> "vi"
            else -> "en"
        }


        val param = listOf(
                "OperatorCode=${token.appId}",
                "lang=${lang}",
                "playerid=${startReq.username}",
                "LoginTokenID=$sign",
                "view=MB",
                "mobile=0",
                "PlayerGroup=default",
                "theme=deafult"

        )

        return ""
    }


}

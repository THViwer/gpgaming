package com.onepiece.treasure.games.live

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.GoldDeluxeClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.MapResultUtil
import com.onepiece.treasure.utils.StringUtil
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
@Service
class GoldDeluxeService: PlatformService() {


    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    private val betDateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
    private val currencyCode = "MYR"


    private fun generatorMessageId(first: String): String {
        return "$first${LocalDateTime.now().format(dateTimeFormat)}${StringUtil.generateNonce(5)}"
    }

    private fun startDoPostXml(data: String): Map<String, Any> {
        val url = "${gameConstant.getDomain(Platform.GoldDeluxe)}/MerchantAPI/ewallet.php"
        val result = okHttpUtil.doPostXml(url = url, data = data, clz = GoldDeluxeValue.Result::class.java)
        check(result.header.errorCode == "0") {OnePieceExceptionCode.PLATFORM_METHOD_FAIL}
        return result.param.data
    }


    override fun register(registerReq: GameValue.RegisterReq): String {

        val token = registerReq.token as GoldDeluxeClientToken

        val messageId = this.generatorMessageId("M")
        val data = """
            <?xml version="1.0"?>
            <Request>
              <Header>
                <Method>cCreateMember</Method>
                <MerchantID>${token.merchantCode}</MerchantID>
                <MessageID>${messageId}</MessageID>
              </Header>
              <Param>
                <UserID>${registerReq.username}</UserID>
                <CurrencyCode>${currencyCode}</CurrencyCode>
                <BetGroup>default</BetGroup>
              </Param>
            </Request>
        """.trimIndent()

        this.startDoPostXml(data = data)
        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {

        val token = balanceReq.token as GoldDeluxeClientToken
        val messageId = this.generatorMessageId("C")

        val data = """
            <?xml version="1.0"?>
            <Request>
              <Header>
                <Method>cCheckClient</Method>
                <MerchantID>${token.merchantCode}</MerchantID>
                <MessageID>${messageId}</MessageID>
              </Header>
              <Param>
                <UserID>${balanceReq.username}</UserID>
                <CurrencyCode>${currencyCode}</CurrencyCode>
                <RequestBetLimit>1</RequestBetLimit>
              </Param>
            </Request>
        """.trimIndent()

        val result = this.startDoPostXml(data = data)
        return MapResultUtil.asBigDecimal(result, "Balance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

        val token = transferReq.token as GoldDeluxeClientToken

        val (messageId, method) = when (transferReq.amount.toDouble() > 0) {
            true -> {
                val messageId = this.generatorMessageId("D")
                messageId to "cDeposit"
            }
            false -> {
                val messageId = this.generatorMessageId("W")
                messageId to "cWithdrawal"
            }
        }

        val data = """
                    <?xml version="1.0"?>
                    <Request>
                      <Header>
                        <Method>${method}</Method>
                        <MerchantID>${token.merchantCode}</MerchantID>
                        <MessageID>${messageId}</MessageID>
                      </Header>
                      <Param>
                        <UserID>${transferReq.username}</UserID>
                        <CurrencyCode>${currencyCode}</CurrencyCode>
                        <Amount>${transferReq.amount.abs()}</Amount>
                        <EnableInGameTransfer>1</EnableInGameTransfer>
                        <GetEndBalance>1</GetEndBalance>
                      </Param>
                    </Request>
                """.trimIndent()

        val result = this.startDoPostXml(data = data)
        return MapResultUtil.asString(result, "TransactionID")
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val token = checkTransferReq.token as GoldDeluxeClientToken
        val messageId = this.generatorMessageId("S")
        val data = """
            <?xml version=”1.0”?>
            <Request>
              <Header>
                <Method>cCheckTransactionStatus</Method>
                <MerchantID>${token.merchantCode}</MerchantID>
                <MessageID>${messageId}</MessageID>
              </Header>
              <Param>
                <MessageID>${checkTransferReq.orderId}</MessageID>
                <UserID>${checkTransferReq.username}</UserID>
                <CurrencyCode>$currencyCode</CurrencyCode>
                </Param>
              </Request>
        """.trimIndent()

        val result = this.startDoPostXml(data = data)
        return MapResultUtil.asString(result, "Status") == "SUCCESS"
    }

    override fun start(startReq: GameValue.StartReq): String {
        val token = startReq.token as GoldDeluxeClientToken

        val loginTokenId = StringUtil.generateNonce(10)
        val signParam = "${token.merchantCode}${loginTokenId}${startReq.username}${currencyCode}"
        val key = DigestUtils.sha256Hex(signParam)

        val lang = when (startReq.language) {
            Language.CN -> "zh-cn"
            Language.EN -> "en"
            Language.TH -> "th"
            Language.ID -> "id"
            Language.VI -> "vi"
            else -> "en"
        }

        val param = listOf(
                "OperatorCode=${token.merchantCode}",
                "lang=${lang}",
                "playerid=${startReq.username}",
                "LoginTokenID=$loginTokenId",
                "Key=$key",
                "view=MB",
                "mobile=0",
                "PlayerGroup=default",
                "theme=deafult"
        )
        val urlParam = param.joinToString(separator = "&")
        val baseUrl = "http://coldsstaging.japaneast.cloudapp.azure.com/COLDS/FlashClient/release/FlashClient_red_gold_GOLD/main.php"
        return "$baseUrl?$urlParam"
    }


    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val clientToken = pullBetOrderReq.token as GoldDeluxeClientToken
        val messageId = this.generatorMessageId("H")

        val data = """
            <?xml version="1.0"?>
            <Request>
              <Header>
                <Method>cGetBetHistory</Method>
                <MerchantID>${clientToken.merchantCode}</MerchantID>
                <MessageID>${messageId}</MessageID>
              </Header>
              <Param>
                <FromTime>${pullBetOrderReq.startTime.format(betDateTimeFormat)}</FromTime>
                <ToTime>${pullBetOrderReq.endTime.format(betDateTimeFormat)}</ToTime>
                <Index>0</Index>
                <ShowBalance>0</ShowBalance>
                <SearchByBalanceTime>1</SearchByBalanceTime>
                <ShowRefID>1</ShowRefID>
                <ShowOdds>1</ShowOdds>
              </Param>
            </Request>
        """.trimIndent()

        val result = this.startDoPostXml(data = data)
        println(result)

        return emptyList()
    }
}
package com.onepiece.gpgaming.games.live

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.GoldDeluxeClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.MapResultUtil
import com.onepiece.gpgaming.utils.StringUtil
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(GoldDeluxeService::class.java)

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    private val betDateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")

    private fun generatorMessageId(first: String): String {
        return "$first${LocalDateTime.now().format(dateTimeFormat)}${StringUtil.generateNonce(5)}"
    }

    private fun startDoPostXml(clientToken: GoldDeluxeClientToken, data: String): Map<String, Any> {
        val url = "${clientToken.apiPath}/MerchantAPI/ewallet.php"
        val result = okHttpUtil.doPostXml(platform = Platform.GoldDeluxe, url = url, data = data, clz = GoldDeluxeValue.Result::class.java)
        check(result.header.errorCode == "0") {
            log.error("goldDeluxe network error: codeId = ${result.header.errorCode}")
            OnePieceExceptionCode.PLATFORM_METHOD_FAIL
        }
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
                <CurrencyCode>${token.currency}</CurrencyCode>
                <BetGroup>default</BetGroup>
              </Param>
            </Request>
        """.trimIndent()

        this.startDoPostXml(clientToken = token, data = data)
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
                <CurrencyCode>${token.currency}</CurrencyCode>
                <RequestBetLimit>1</RequestBetLimit>
              </Param>
            </Request>
        """.trimIndent()

        val result = this.startDoPostXml(clientToken = token, data = data)
        return MapResultUtil.asBigDecimal(result, "Balance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {

        val token = transferReq.token as GoldDeluxeClientToken

        val method = if (transferReq.amount.toDouble() > 0) "cDeposit" else "cWithdrawal"

        val data = """
                    <?xml version="1.0"?>
                    <Request>
                      <Header>
                        <Method>${method}</Method>
                        <MerchantID>${token.merchantCode}</MerchantID>
                        <MessageID>${transferReq.orderId}</MessageID>
                      </Header>
                      <Param>
                        <UserID>${transferReq.username}</UserID>
                        <CurrencyCode>${token.currency}</CurrencyCode>
                        <Amount>${transferReq.amount.abs()}</Amount>
                        <EnableInGameTransfer>1</EnableInGameTransfer>
                        <GetEndBalance>1</GetEndBalance>
                      </Param>
                    </Request>
                """.trimIndent()

        val result = this.startDoPostXml(clientToken = token, data = data)
        val platformOrderId = MapResultUtil.asString(result, "TransactionID")
        val balance = MapResultUtil.asBigDecimal(result, "Balance")
        return GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        val token = checkTransferReq.token as GoldDeluxeClientToken
        val messageId = this.generatorMessageId("S")
        val data = """
            <Request>
              <Header>
                <Method>cCheckTransactionStatus</Method>
                <MerchantID>${token.merchantCode}</MerchantID>
                <MessageID>${messageId}</MessageID>
              </Header>
              <Param>
                <MessageID>${checkTransferReq.orderId}</MessageID>
                <UserID>${checkTransferReq.username}</UserID>
                <CurrencyCode>${token.currency}</CurrencyCode>
                </Param>
              </Request>
        """.trimIndent()

        val result = this.startDoPostXml(clientToken = token, data = data)
        val successful = MapResultUtil.asString(result, "Status") == "SUCCESS"
        return GameValue.TransferResp.of(successful)
    }

    override fun start(startReq: GameValue.StartReq): String {
        val token = startReq.token as GoldDeluxeClientToken

        val loginTokenId = StringUtil.generateNonce(10)
        val signParam = "${token.merchantCode}${loginTokenId}${token.key}${startReq.username}${token.currency}"
        val key = DigestUtils.sha256Hex(signParam)

        val lang = when (startReq.language) {
            Language.CN -> "zh-cn"
            Language.EN -> "en"
            Language.TH -> "th"
            Language.ID -> "id"
            Language.VI -> "vi"
            else -> "en"
        }

        val isMobile = if (startReq.launch == LaunchMethod.Wap) "mobile=1" else "mobile=0"

        val param = listOf(
                "OperatorCode=${token.merchantCode}",
                "lang=${lang}",
                "Currency=${token.currency}",
                "playerid=${startReq.username}",
                "LoginTokenID=$loginTokenId",
                "Key=$key",
                "view=MB",
                isMobile,
                "PlayerGroup=default",
                "theme=deafult"
        )
        val urlParam = param.joinToString(separator = "&")
        return "${token.gamePath}?$urlParam"
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
                <ShowValidBetAmount>1</ShowValidBetAmount>
              </Param>
            </Request>
        """.trimIndent()


        val url = "${clientToken.apiOrderPath}/MerchantAPI/report.php"
        val result = okHttpUtil.doPostXml(platform = Platform.GoldDeluxe, url = url, data = data, clz = GoldDeluxeValue.BetResult::class.java)

        if (result.param.totalRecord == 0) return emptyList()

        return result.param.betInfoList.map { betInfo ->
            val bet = betInfo.mapUtil
            val username = bet.asString("UserID")
            val orderId = bet.asString("BetID")
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.GoldDeluxe, platformUsername = username)
            val betTime = bet.asLocalDateTime("BetTime", betDateTimeFormat)
            val settleTime = bet.asLocalDateTime("BalanceTime", betDateTimeFormat)
            val betAmount = bet.asBigDecimal("BetAmount")
            val winAmount = bet.asBigDecimal("WinLoss")
            val validBetAmount = bet.asBigDecimal("ValidBetAmount")

            val originData = objectMapper.writeValueAsString(bet.data)

            BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.GoldDeluxe, orderId = orderId, betAmount = betAmount,
                    winAmount = winAmount, originData = originData, betTime = betTime, settleTime = settleTime, validAmount = validBetAmount)

        }
    }
}


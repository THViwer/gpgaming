package com.onepiece.gpgaming.games.sport

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.model.token.LbcClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.MapUtil
import okhttp3.FormBody
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class LbcService : PlatformService() {

    private val log = LoggerFactory.getLogger(LbcService::class.java)

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    fun startGetJson(clientToken: LbcClientToken, method: String, formBody: FormBody): MapUtil {
        val url = "${clientToken.apiDomain}/api/${method}"
        val result = okHttpUtil.doPostForm(url = url, body = formBody, clz = LbcValue.Result::class.java)

        check(result.errorCode == 0) {
            log.error("lbc network error: errorCode = ${result.errorCode}, msg = ${result.message}")
            OnePieceExceptionCode.DATA_FAIL
        }
        return result.mapUtil
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as LbcClientToken


        val username = "${clientToken.memberCode}_${registerReq.username}"
        /**
         * 1 马来盘 2 香港盘 3 欧洲盘 4 印尼盘 5 美国盘
         */
        val body = FormBody.Builder()
                .add("vendor_id", clientToken.vendorId)
                .add("Vendor_Member_ID", username)
                .add("OperatorId", clientToken.memberCode)
                .add("UserName", username)
                .add("Currency", clientToken.currency) //TODO 测试环境只能先用20(UUS) 以后替换成2(MYR)
                .add("OddsType", "1")
                .add("MaxTransfer", "999999")
                .add("MinTransfer", "1")
                .build()
        this.startGetJson(clientToken = clientToken, method = "CreateMember", formBody = body)
        return username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as LbcClientToken

        val body = FormBody.Builder()
                .add("vendor_id", clientToken.vendorId)
                .add("vendor_member_ids", balanceReq.username)
                .add("wallet_id", "1") // 钱包识别码, 1: Sportsbook/ 5: AG/ 6: GD
                .build()

        val mapUtil = this.startGetJson(clientToken = clientToken, method = "CheckUserBalance", formBody = body)
        return mapUtil.asList("Data").first().data["balance"]?.toString()?.toBigDecimal() ?: BigDecimal.ZERO
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {
        val clientToken = transferReq.token as LbcClientToken

        val direction = if (transferReq.amount.toDouble() > 0) 1 else 0
        val body = FormBody.Builder()
                .add("vendor_id", clientToken.vendorId)
                .add("vendor_member_id", transferReq.username)
                .add("vendor_trans_id", transferReq.orderId)
                .add("amount", "${transferReq.amount.abs()}")
                .add("currency", "20") // 固定
                .add("direction", "$direction")
                .add("wallet_id", "1") // 钱包识别码, 1: Sportsbook/ 5: AG/ 6: GD
                .build()

        val mapUtil = this.startGetJson(clientToken = clientToken, method = "FundTransfer", formBody = body)
        val platformOrderId = mapUtil.asMap("Data").asString("trans_id")
        return GameValue.TransferResp.successful()
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        val clientToken = checkTransferReq.token as LbcClientToken

        val body = FormBody.Builder()
                .add("vendor_id", clientToken.vendorId)
                .add("vendor_trans_id", checkTransferReq.orderId)
                .add("wallet_id", "1") // 钱包识别码, 1: Sportsbook/ 5: AG/ 6: GD
                .build()
        val mapUtil = this.startGetJson(clientToken = clientToken, method = "CheckFundTransfer", formBody = body)
        val successful = mapUtil.asMap("Data").data["status"] == 0
        return GameValue.TransferResp.of(successful)
    }

    override fun start(startReq: GameValue.StartReq): String {
        val clientToken = startReq.token as LbcClientToken

        val body = FormBody.Builder()
                .add("vendor_id", clientToken.vendorId)
                .add("vendor_member_id", startReq.username)
                .build()
        val mapUtil = this.startGetJson(clientToken = clientToken, method = "Login", formBody = body)
        val token = mapUtil.asString("Data")

        val lang = when (startReq.language) {
            Language.CN -> "cs"
            Language.TH -> "th"
            Language.ID -> "id"
            Language.VI -> "vn"
            Language.EN -> "en"
            else -> "en"
        }


        return "http://smartsbtest.gpgaming88.com/deposit_processlogin.aspx?lang=${lang}&token=${token}&skincolor=bl001"
    }

    override fun startDemo(token: ClientToken, language: Language, launch: LaunchMethod): String {
        val lang = when (language) {
            Language.CN -> "cs"
            Language.TH -> "th"
            Language.ID -> "id"
            Language.VI -> "vn"
            Language.EN -> "en"
            else -> "en"
        }

        return when (launch) {
            LaunchMethod.Wap -> "http://c.gsoft888.net/vender.aspx?lang=${lang}&OType=1&skincolor=bl00"
            else -> "http://smartsbtest.gpgaming88.com/deposit_processlogin.aspx?lang=${lang}&skincolor=bl001"
        }

    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val clientToken = pullBetOrderReq.token as LbcClientToken

        return this.pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.Lbc) { startId ->
            val body = FormBody.Builder()
                    .add("vendor_id", clientToken.vendorId)
                    .add("version_key", startId)
                    .build()
            val mapUtil = startGetJson(clientToken = clientToken, method = "GetBetDetail", formBody = body)
            val d = mapUtil.asMap("Data")
            val lastVersionKey = d.asString("last_version_key")
            val orders = d.asList("BetDetails").filter {
                val ticketStatus = it.asString("ticket_status")
                ticketStatus == "half won" || ticketStatus == "half lose" || ticketStatus == "won"
            }.map { bet ->
                val orderId = bet.asString("trans_id")
                val username = bet.asString("vendor_member_id")
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Lbc, platformUsername = username)
                val betAmount = bet.asBigDecimal("stake")
                val winAmount = bet.asBigDecimal("winlost_amount")
                val betTime = bet.asLocalDateTime("transaction_time", dateTimeFormat)
                val settleTime = bet.asLocalDateTime("winlost_datetime", dateTimeFormat)

                val originData = objectMapper.writeValueAsString(bet.data)
                BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, betAmount = betAmount, winAmount = winAmount,
                        platform = Platform.Lbc, betTime = betTime, settleTime = settleTime, originData = originData)
            }

            val virtualSportDetails = d.asList("BetVirtualSportDetails").filter {
                val ticketStatus = it.asString("ticket_status")
                ticketStatus == "half won" || ticketStatus == "half lose" || ticketStatus == "won"
            }.map { bet ->
                val orderId = bet.asString("trans_id")
                val username = bet.asString("vendor_member_id")
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Lbc, platformUsername = username)
                val betAmount = bet.asBigDecimal("stake")
                val winAmount = bet.asBigDecimal("winlost_amount")
                val betTime = bet.asString("transaction_time").substring(0, 19).let { LocalDateTime.parse(it) }
                val settleTime = bet.asString("winlost_datetime").substring(0, 19).let { LocalDateTime.parse(it) }

                val originData = objectMapper.writeValueAsString(bet.data)
                BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, betAmount = betAmount, winAmount = winAmount,
                        platform = Platform.Lbc, betTime = betTime, settleTime = settleTime, originData = originData)
            }

            lastVersionKey to orders.plus(virtualSportDetails)
        }

    }
}
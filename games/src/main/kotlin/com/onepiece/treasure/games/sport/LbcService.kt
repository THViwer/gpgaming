package com.onepiece.treasure.games.sport

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.ClientToken
import com.onepiece.treasure.beans.model.token.LbcClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.MapUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@Service
class LbcService : PlatformService() {

    private val log = LoggerFactory.getLogger(LbcService::class.java)

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

    fun startGetJson(method: String, data: List<String>): MapUtil {
        val urlParam = data.joinToString(separator = "&")
        val url = "${GameConstant.getDomain(Platform.Lbc)}/api/${method}?$urlParam"
        val result = okHttpUtil.doGet(url = url, clz = LbcValue.Result::class.java)

        check(result.errorCode == 0) {
            log.error("请求方法:$method:", result.message)
            OnePieceExceptionCode.DATA_FAIL
        }
        return result.mapUtil
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as LbcClientToken

        /**
         * 1 马来盘 2 香港盘 3 欧洲盘 4 印尼盘 5 美国盘
         */
        val data = listOf(
                "vendor_id=${clientToken.vendorId}",
                "Vendor_Member_ID=${registerReq.clientId}",
                "OperatorId=${registerReq.memberId}",
                "UserName=${registerReq.username}",
                "Currency=20", // 固定
                "OddsType=1",
                "MaxTransfer=99999",
                "MinTransfer=1"
        )

        this.startGetJson(method = "CreateMember", data = data)
        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as LbcClientToken

        val data = listOf(
                "vendor_id=${clientToken.vendorId}",
                "vendor_member_ids=${balanceReq.username}",
                "wallet_id=1" // 钱包识别码, 1: Sportsbook/ 5: AG/ 6: GD
        )

        val mapUtil = this.startGetJson(method = "CheckUserBalance", data = data)
        return mapUtil.asMap("data").asBigDecimal("balance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        val clientToken = transferReq.token as LbcClientToken

        val direction = if (transferReq.amount.toDouble() > 0) 1 else 0
        val data = listOf(
                "vendor_id=${clientToken.vendorId}",
                "vendor_member_id=${transferReq.username}",
                "vendor_trans_id=${transferReq.orderId}",
                "amount=${transferReq.amount.abs()}",
                "currency=20", // 固定
                "direction=${direction}",
                "wallet_id=1" // 钱包识别码, 1: Sportsbook/ 5: AG/ 6: GD
        )
        val mapUtil = this.startGetJson(method = "FundTransfer", data = data)
        return mapUtil.asMap("data").asString("trans_id")
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val clientToken = checkTransferReq.token as LbcClientToken

        val data = listOf(
                "vendor_id=${clientToken.vendorId}",
                "vendor_trans_id=${checkTransferReq.orderId}",
                "wallet_id=1" // 钱包识别码, 1: Sportsbook/ 5: AG/ 6: GD
        )
        val mapUtil = this.startGetJson(method = "CheckFundTransfer", data = data)
        return mapUtil.asMap("data").data["trans_id"] != null
    }

    override fun start(startReq: GameValue.StartReq): String {
        val clientToken = startReq.token as LbcClientToken

        val data = listOf(
                "vendor_id=${clientToken.vendorId}",
                "vendor_member_id=${startReq.username}"
        )
        val mapUtil = this.startGetJson(method = "Login", data = data)
        val token = mapUtil.asMap("data").asString("data")

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

    override fun startDemo(token: ClientToken, language: Language): String {
        val lang = when (language) {
            Language.CN -> "cs"
            Language.TH -> "th"
            Language.ID -> "id"
            Language.VI -> "vn"
            Language.EN -> "en"
            else -> "en"
        }
        return "http://smartsbtest.gpgaming88.com/deposit_processlogin.aspx?lang=${lang}&skincolor=bl001"
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val clientToken = pullBetOrderReq.token as LbcClientToken

        return this.pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.Lbc) { startId ->

            val data = listOf(
                    "vendor_id=${clientToken.vendorId}",
                    "version_key=$startId"
            )
            val mapUtil = startGetJson(method = "GetBetDetail", data = data)
            val d = mapUtil.asMap("data")
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
                val settleTime = bet.asLocalDateTime("settlement_time", dateTimeFormat)

                val originData = objectMapper.writeValueAsString(bet.data)
                BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, betAmount = betAmount, winAmount = winAmount,
                        platform = Platform.Lbc, betTime = betTime, settleTime = settleTime, originData = originData)

            }

            lastVersionKey to orders
        }

    }
}
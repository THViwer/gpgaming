package com.onepiece.gpgaming.games.sport

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.model.token.LbcClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Exception
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class LbcService : PlatformService() {

    private val log = LoggerFactory.getLogger(LbcService::class.java)

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    fun doPostForm(clientToken: LbcClientToken, method: String, formParam: Map<String, Any>): OKResponse {
        val url = "${clientToken.apiPath}/api/${method}"

        val okParam = OKParam.ofPost(url = url, param = "", formParam = formParam)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)
        if (!okResponse.ok) return okResponse

        val ok = try {
            val errorCode = okResponse.asInt("error_code")
            errorCode == 0
        } catch (e: Exception) {
            false
        }

        return okResponse.copy(ok = ok)
    }


    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val clientToken = registerReq.token as LbcClientToken


        /**
         * 1 马来盘 2 香港盘 3 欧洲盘 4 印尼盘 5 美国盘
         */
        val param = mapOf(
                "vendor_id" to clientToken.vendorId,
                "Vendor_Member_ID" to "${clientToken.memberCode}_${registerReq.username}",
                "OperatorId" to clientToken.memberCode,
                "UserName" to "${clientToken.memberCode}_${registerReq.username}",
                "Currency" to clientToken.currency, //TODO 测试环境只能先用20(UUS) 以后替换成2(MYR)
                "OddsType" to "1",
                "MaxTransfer" to "999999",
                "MinTransfer"  to "1"
        )

        val okResponse = this.doPostForm(clientToken = clientToken, method = "CreateMember", formParam = param)
        return this.bindGameResponse(okResponse) {
            registerReq.username
        }
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as LbcClientToken

        val param = mapOf(
                "vendor_id" to clientToken.vendorId,
                "vendor_member_ids" to "${clientToken.memberCode}_${balanceReq.username}",
                "wallet_id" to "1" // 钱包识别码, 1: Sportsbook/ 5: AG/ 6: GD

        )

        val okResponse = this.doPostForm(clientToken = clientToken, method = "CheckUserBalance", formParam = param)
        return this.bindGameResponse(okResponse) {
            it.asList("Data").first().data["balance"]?.toString()?.toBigDecimal() ?: BigDecimal.ZERO
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = transferReq.token as LbcClientToken

        val direction = if (transferReq.amount.toDouble() > 0) 1 else 0

        val param = mapOf(
                "vendor_id" to clientToken.vendorId,
                "vendor_member_id" to "${clientToken.memberCode}_${transferReq.username}",
                "vendor_trans_id" to "${clientToken.memberCode}_${transferReq.orderId}",
                "amount" to "${transferReq.amount.abs()}",
                "currency" to clientToken.currency, // 固定
                "direction" to "$direction",
                "wallet_id" to "1" // 钱包识别码, 1: Sportsbook/ 5: AG/ 6: GD
        )

        val okResponse = this.doPostForm(clientToken = clientToken, method = "FundTransfer", formParam = param)
        return this.bindGameResponse(okResponse = okResponse) {
            GameValue.TransferResp.successful()
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = checkTransferReq.token as LbcClientToken

        val param = mapOf(
                "vendor_id" to clientToken.vendorId,
                "vendor_trans_id" to "${clientToken.memberCode}_${checkTransferReq.orderId}",
                "wallet_id" to "1" // 钱包识别码, 1: Sportsbook/ 5: AG/ 6: GD
        )
        val okResponse = this.doPostForm(clientToken = clientToken, method = "CheckFundTransfer", formParam = param)
        return this.bindGameResponse(okResponse) {
            val successful = it.asMap("Data").data["status"] == 0
            GameValue.TransferResp.of(successful)
        }
    }

    override fun start(startReq: GameValue.StartReq): GameResponse<String> {
        val clientToken = startReq.token as LbcClientToken

        val param = mapOf(
                "vendor_id" to clientToken.vendorId,
                "vendor_member_id" to "${clientToken.memberCode}_${startReq.username}"
        )

        val mapUtil = this.doPostForm(clientToken = clientToken, method = "Login", formParam = param)
        val token = mapUtil.asString("Data")

        val lang = when (startReq.language) {
            Language.CN -> "cs"
            Language.TH -> "th"
            Language.ID -> "id"
            Language.VI -> "vn"
            Language.EN -> "en"
            else -> "en"
        }

        val path = when (startReq.launch) {
            LaunchMethod.Wap -> "https://ismart.l0030.ig128.com/deposit_processlogin.aspx?lang=${lang}&token=${token}&skincolor=bl001"
            else -> "https://mkt.l0030.ig128.com/deposit_processlogin.aspx?lang=${lang}&token=${token}&skincolor=bl001"
        }
        return GameResponse.of(data = path)
    }

    override fun startDemo(token: ClientToken, language: Language, launch: LaunchMethod): GameResponse<String> {
        val lang = when (language) {
            Language.CN -> "cs"
            Language.TH -> "th"
            Language.ID -> "id"
            Language.VI -> "vn"
            Language.EN -> "en"
            else -> "en"
        }

        val path = when (launch) {
            LaunchMethod.Wap -> "https://ismart.l0030.ig128.com/DepositLogin/bfindex?lang=${lang}&OType=1&skincolor=bl00"
            else -> "https://mkt.l0030.ig128.com/NewIndex"
        }
        return GameResponse.of(data = path)

    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {
        val clientToken = pullBetOrderReq.token as LbcClientToken

        return this.pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.Lbc) { startId ->

            val param = mapOf(
                    "vendor_id" to clientToken.vendorId,
                    "version_key" to startId
            )

            val okResponse = doPostForm(clientToken = clientToken, method = "GetBetDetail", formParam = param)
            var lastVersionKey: String = ""

            val gameResponse = this.bindGameResponse(okResponse = okResponse) { mapUtil ->
                val d = mapUtil.asMap("Data")
                lastVersionKey = d.asString("last_version_key")
                val orders = d.asList("BetDetails").filter {
                    val ticketStatus = it.asString("ticket_status")

                    ticketStatus != "waiting" && ticketStatus != "running" && ticketStatus != "reject" && ticketStatus != "refund"
                    // ticketStatus == "half won" || ticketStatus == "half lose" || ticketStatus == "won"
                }.map { bet ->
                    val orderId = bet.asString("trans_id")
                    val username = bet.asString("vendor_member_id")
                    val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Lbc, platformUsername = username, prefix = clientToken.memberCode)
                    val betAmount = bet.asBigDecimal("stake")
                    val winAmount = bet.asBigDecimal("winlost_amount")
                    val betTime = bet.asLocalDateTime("transaction_time")
                    val settleTime = bet.asLocalDateTime("winlost_datetime")

                    val originData = objectMapper.writeValueAsString(bet.data)
                    BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, betAmount = betAmount, winAmount = winAmount,
                            platform = Platform.Lbc, betTime = betTime, settleTime = settleTime, originData = originData, validAmount = betAmount)
                }

                val virtualSportDetails = d.asList("BetVirtualSportDetails").filter {
                    val ticketStatus = it.asString("ticket_status")
                    // ticketStatus == "half won" || ticketStatus == "half lose" || ticketStatus == "won"
                    ticketStatus != "waiting" && ticketStatus != "running" && ticketStatus != "reject" && ticketStatus != "refund"
                }.map { bet ->
                    val orderId = bet.asString("trans_id")
                    val username = bet.asString("vendor_member_id")
                    val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Lbc, platformUsername = username, prefix = clientToken.memberCode)
                    val betAmount = bet.asBigDecimal("stake")
                    val winAmount = bet.asBigDecimal("winlost_amount")
                    val betTime = bet.asString("transaction_time").substring(0, 19).let { LocalDateTime.parse(it) }
                    val settleTime = bet.asString("winlost_datetime").substring(0, 19).let { LocalDateTime.parse(it) }

                    val originData = objectMapper.writeValueAsString(bet.data)
                    BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, betAmount = betAmount, winAmount = winAmount,
                            platform = Platform.Lbc, betTime = betTime, settleTime = settleTime, originData = originData, validAmount = betAmount)
                }

                orders.plus(virtualSportDetails)
            }

            BetNextIdData(nextId = lastVersionKey, gameResponse = gameResponse)
        }

    }
}
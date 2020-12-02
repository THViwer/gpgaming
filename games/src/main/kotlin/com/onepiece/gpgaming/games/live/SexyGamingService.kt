package com.onepiece.gpgaming.games.live

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.U9RequestStatus
import com.onepiece.gpgaming.beans.model.token.SexyGamingClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.MapUtil
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import okhttp3.FormBody
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime


@Service
class SexyGamingService : PlatformService() {

    private val log = LoggerFactory.getLogger(SexyGamingService::class.java)

    fun doPostForm(clientToken: SexyGamingClientToken, path: String, data: Map<String, String>): OKResponse {

        log.info("sexyGaming 请求数据：$data")

//        val url = "${clientToken.apiPath}$method"

        val body = FormBody.Builder()
        data.forEach {
            body.add(it.key, it.value)
        }

        val okParam = OKParam.ofPost(url = path, param = "", formParam = data)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)
        if (!okResponse.ok) return okResponse

        val status = try {
            when (okResponse.asString("status")) {
                "0000", "1" -> U9RequestStatus.OK
                else -> U9RequestStatus.Fail
            }
        } catch (e: Exception) {
            U9RequestStatus.Fail
        }

        return okResponse.copy(status = status)
    }

    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {

        val clientToken = registerReq.token as SexyGamingClientToken

        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "userId" to registerReq.username,
                "currency" to clientToken.currency,
                "betLimit" to clientToken.betLimit

        )

        val okResponse = this.doPostForm(clientToken = clientToken, path = "${clientToken.apiPath}/wallet/createMember", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            registerReq.username
        }
    }


    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as SexyGamingClientToken

        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "userIds" to balanceReq.username
        )
        val okResponse = this.doPostForm(clientToken = clientToken, path = "${clientToken.apiPath}/wallet/getBalance", data = data)

        return this.bindGameResponse(okResponse = okResponse) {
            it.asList("results").first().asBigDecimal("balance")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = transferReq.token as SexyGamingClientToken

        val okResponse = when (transferReq.amount.toDouble() > 0) {
            true -> {

                val data = mapOf(
                        "cert" to clientToken.cert,
                        "agentId" to clientToken.agentId,
                        "userId" to transferReq.username,
                        "transferAmount" to "${transferReq.amount}",
                        "txCode" to transferReq.orderId
                )
                this.doPostForm(clientToken = clientToken, path = "${clientToken.apiPath}/wallet/deposit", data = data)
            }
            else -> {
                val data = mapOf(
                        "cert" to clientToken.cert,
                        "userId" to transferReq.username,
                        "agentId" to clientToken.agentId,
                        "txCode" to transferReq.orderId,
                        "withdrawType" to "0",
                        "transferAmount" to "${transferReq.amount.abs()}"
                )
                this.doPostForm(clientToken = clientToken, path = "${clientToken.apiPath}/wallet/withdraw", data = data)
            }
        }

        return this.bindGameResponse(okResponse = okResponse) {
            val platformOrderId = it.asString("txCode")
            val balance = it.asBigDecimal("currentBalance")
            GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
        }

    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = checkTransferReq.token as SexyGamingClientToken

        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "txCode" to checkTransferReq.orderId
        )
        val okResponse = this.doPostForm(clientToken = clientToken, path = "${clientToken.apiPath}/wallet/checkTransferOperation", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            val successful = it.data["txStatus"] == 1
            GameValue.TransferResp.of(successful = successful)
        }

    }

    override fun start(startReq: GameValue.StartReq): GameResponse<String> {
        val clientToken = startReq.token as SexyGamingClientToken

        val isMobileLogin = startReq.launch == LaunchMethod.Wap

        val lang = when (startReq.language) {
            Language.EN -> "en"
            Language.CN -> "cn"
            Language.TH -> "th"
            else -> "en"
        }

        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "userId" to startReq.username,
                "isMobileLogin" to "$isMobileLogin",
                "externalURL" to startReq.redirectUrl,
                "language" to lang,
                "gameType" to "LIVE", // 启动真人
                "platform" to "SEXYBCRT"
        )
        val okResponse = this.doPostForm(clientToken = clientToken, path = "${clientToken.apiPath}/wallet/doLoginAndLaunchGame", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asString("url")
        }
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {
        val clientToken = pullBetOrderReq.token as SexyGamingClientToken

        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "timeFrom" to "${pullBetOrderReq.startTime.toString().substring(0, 19)}+08:00",
                "platform" to clientToken.platform,
                "status" to "1" //已结算
        )

        val okResponse = this.doPostForm(clientToken = clientToken, path = "${clientToken.orderApiPath}/fetch/getTransactionByUpdateDate", data = data)

        return this.bindGameResponse(okResponse = okResponse) {
            it.asList("transactions").map { bet ->

                val orderId = bet.asString("roundId")
                val username = bet.asString("userId")
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.SexyGaming, platformUsername = username)
                val betAmount = try {
                    bet.asBigDecimal("betAmt")
                } catch (e: Exception) {
                    bet.asBigDecimal("betAmount")
                }

                val realBetAmount = bet.asBigDecimal("realBetAmount")
                val payout = try {
                    bet.asBigDecimal("winAmt")
                } catch (e: Exception) {
                    bet.asBigDecimal("winAmount")
                }


                val settleTime = bet.asString("updateTime").substring(0, 19).let { LocalDateTime.parse(it) }

                val betTime = try {
                    bet.asString("createTime").substring(0, 19).let { LocalDateTime.parse(it) }
                } catch (e: Exception) {
                    bet.asString("txTime").substring(0, 19).let { LocalDateTime.parse(it) }
                }

                val originData = objectMapper.writeValueAsString(bet.data)

                BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, betAmount = betAmount, payout = payout, betTime = betTime,
                        settleTime = settleTime, originData = originData, platform = Platform.SexyGaming, validAmount = realBetAmount)
            }

        }
    }

    fun getSummaryByTxTimeHour(clientToken: SexyGamingClientToken, startDate: LocalDate): MapUtil {
        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "startTime" to "${startDate}T00+08:00",
                "endTime" to "${startDate.plusDays(1)}T00+08:00"
        )
        val okResponse = this.doPostForm(clientToken = clientToken, path = "${clientToken.orderApiPath}/fetch/getSummaryByTxTimeHour", data = data)
        return okResponse.asList("transactions").firstOrNull() ?: MapUtil.instance(hashMapOf())
    }

}
package com.onepiece.gpgaming.games.live

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.SexyGamingClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.MapUtil
import okhttp3.FormBody
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Exception
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime


@Service
class SexyGamingService: PlatformService() {

    private val log = LoggerFactory.getLogger(SexyGamingService::class.java)

    fun startGetJson(clientToken: SexyGamingClientToken, method: String, data: Map<String, String>): MapUtil {

        log.info("sexyGaming 请求数据：$data")

        val url = "${clientToken.apiPath}$method"

        val body = FormBody.Builder()
        data.forEach {
            body.add(it.key, it.value)
        }

        val result = okHttpUtil.doPostForm(platform = Platform.SexyGaming, url = url, body = body.build(), clz = SexyGamingValue.Result::class.java)
        check(result.status == "0000" || result.status == "1" ) {
            log.error("sexyGaming network error: status = ${result.status}, desc = ${result.desc}")
            OnePieceExceptionCode.PLATFORM_DATA_FAIL
        }

        return result.mapUtil
    }

    override fun register(registerReq: GameValue.RegisterReq): String {

        val clientToken = registerReq.token as SexyGamingClientToken

        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "userId" to registerReq.username,
                "currency" to clientToken.currency,
                "betLimit" to clientToken.betLimit

        )

        this.startGetJson(clientToken = clientToken, method = "/wallet/createMember", data = data)
        return registerReq.username
    }


    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as SexyGamingClientToken

        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "userIds" to balanceReq.username
        )
        val mapUtil = this.startGetJson(clientToken = clientToken, method = "/wallet/getBalance", data = data)


        return mapUtil.asMap("results").data.entries.first().value.toString().toBigDecimal()
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {
        val clientToken = transferReq.token as SexyGamingClientToken

        val mapUtil = when (transferReq.amount.toDouble() > 0) {
            true -> {

                val data = mapOf(
                        "cert" to clientToken.cert,
                        "agentId" to clientToken.agentId,
                        "userId" to transferReq.username,
                        "transferAmount" to "${transferReq.amount}",
                        "txCode" to transferReq.orderId
                )
                this.startGetJson(clientToken = clientToken, method = "/wallet/deposit", data = data)
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
                this.startGetJson(clientToken = clientToken, method = "/wallet/withdraw", data = data)
            }
        }
        val platformOrderId =  mapUtil.asString("txCode")
        val balance = mapUtil.asBigDecimal("currentBalance")
        return GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        val clientToken = checkTransferReq.token as SexyGamingClientToken

        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "txCode" to checkTransferReq.orderId
        )
        val mapUtil = this.startGetJson(clientToken = clientToken, method = "/wallet/checkTransferOperation", data = data)
        val successful = mapUtil.data["txStatus"] == 1
        return GameValue.TransferResp.of(successful = successful)
    }

    override fun start(startReq: GameValue.StartReq): String {
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
        val mapUtil = this.startGetJson(clientToken = clientToken, method = "/wallet/doLoginAndLaunchGame", data = data)
        //TODO 判断语言设置启动
        return mapUtil.asString("url")
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val clientToken = pullBetOrderReq.token as SexyGamingClientToken

        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "timeFrom" to "${pullBetOrderReq.startTime.toString().substring(0, 19)}+08:00",
                "status" to "1" //已结算
        )

        val mapUtil = this.startGetJson(clientToken = clientToken, method = "/wallet/getTransactionByUpdateDate", data = data)

        return mapUtil.asList("transactions").map { bet ->

            val orderId = bet.asString("roundId")
            val username = bet.asString("userId")
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.SexyGaming, platformUsername = username)
            val betAmount = try {
                bet.asBigDecimal("betAmt")
            } catch (e: Exception) {
                bet.asBigDecimal("betAmount")
            }

            val realBetAmount = bet.asBigDecimal("realBetAmount")
            val winAmount = try {
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

            BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, betAmount = betAmount, winAmount = winAmount, betTime = betTime,
                    settleTime = settleTime, originData = originData, platform = Platform.SexyGaming, validAmount = realBetAmount)
        }
    }

    fun getSummaryByTxTimeHour(clientToken: SexyGamingClientToken, startDate: LocalDate): MapUtil {
        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "startTime" to "${startDate}T00+08:00",
                "endTime" to "${startDate.plusDays(1)}T00+08:00"
        )
        val mapUtil = this.startGetJson(clientToken = clientToken, method = "/wallet/getSummaryByTxTimeHour", data = data)
        return mapUtil.asList("transactions").firstOrNull() ?: MapUtil.instance(hashMapOf())
    }

}
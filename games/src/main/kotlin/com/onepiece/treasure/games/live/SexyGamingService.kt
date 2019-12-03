package com.onepiece.treasure.games.live

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.SexyGamingClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.MapUtil
import okhttp3.FormBody
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class SexyGamingService: PlatformService() {

    fun startGetJson(method: String, data: Map<String, String>): MapUtil {

        val url = "${gameConstant.getDomain(Platform.SexyGaming)}$method"

        val body = FormBody.Builder()
        data.forEach {
            body.add(it.key, it.value)
        }

        val result = okHttpUtil.doPostForm(url = url, body = body.build(), clz = SexyGamingValue.Result::class.java)
        check(result.status == "0000" || result.status == "1" ) { OnePieceExceptionCode.PLATFORM_DATA_FAIL }

        return result.mapUtil
    }

    override fun register(registerReq: GameValue.RegisterReq): String {

        val clientToken = registerReq.token as SexyGamingClientToken

        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "userId" to registerReq.username,
                "currency" to clientToken.currency,
//                "betLimit" to "{\"SEXYBCRT\":{\"LIVE\":{\"limitId\":[280101,280102,280103,280104,280105,280106,280107]}}}"
//                "betLimit" to "{\"SEXYBCRT\":{\"LIVE\":{\"limitId\":[280101,280102]}},\"SV388\":{\"LIVE\":{\"maxbet\":1000,\"minbet\":100,\"mindraw\":100,\"matchlimit\":1000,\"maxdraw\":100}},\"VENUS\":{\"LIVE\":{\"limitId\":[280101,280102]}}}"
                "betLimit" to clientToken.betLimit

        )

        this.startGetJson(method = "/wallet/createMember", data = data)
        return registerReq.username
    }


    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as SexyGamingClientToken

        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "userIds" to balanceReq.username
        )
        val mapUtil = this.startGetJson(method = "/wallet/getBalance", data = data)


        return mapUtil.asMap("results").data.entries.first().value.toString().toBigDecimal()
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        val clientToken = transferReq.token as SexyGamingClientToken

        val mapUtil = when (transferReq.amount.toDouble() > 0) {
            true -> {

                val data = mapOf(
                        "cert" to clientToken.cert,
                        "agentId" to clientToken.agentId,
                        "userId" to transferReq.username,
                        "amount" to "${transferReq.amount}",
                        "txCode" to transferReq.orderId
                )
                this.startGetJson(method = "/wallet/deposit", data = data)
            }
            else -> {
                val data = mapOf(
                        "cert" to clientToken.cert,
                        "userId" to transferReq.username,
                        "agentId" to clientToken.agentId,
                        "txCode" to transferReq.orderId,
                        "transferAmt" to "${transferReq.amount.abs()}"
                )
                this.startGetJson(method = "/wallet/withdraw", data = data)
            }
        }
        return mapUtil.asString("txCode")
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val clientToken = checkTransferReq.token as SexyGamingClientToken

        val data = mapOf(
                "cert" to clientToken.cert,
                "agentId" to clientToken.agentId,
                "txCode" to checkTransferReq.orderId
        )
        val mapUtil = this.startGetJson(method = "/wallet/checkTransferOperation", data = data)
        return mapUtil.data["transferAmt"] != null
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
        val mapUtil = this.startGetJson(method = "/wallet/doLoginAndLaunchGame", data = data)
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

        val mapUtil = this.startGetJson(method = "/wallet/getTransactionByUpdateDate", data = data)

        return mapUtil.asList("transactions").map { bet ->

            val orderId = bet.asString("roundId")
            val username = bet.asString("userId")
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.SexyGaming, platformUsername = username)
            val betAmount = bet.asBigDecimal("betAmt")
            val winAmount = bet.asBigDecimal("winAmt")
            val betTime = bet.asString("createTime").substring(0, 19).let { LocalDateTime.parse(it) }
            val settleTime = bet.asString("updateTime").substring(0, 19).let { LocalDateTime.parse(it) }

            val originData = objectMapper.writeValueAsString(bet.data)

            BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, betAmount = betAmount, winAmount = winAmount, betTime = betTime,
                    settleTime = settleTime, originData = originData, platform = Platform.SexyGaming)
        }

    }

}
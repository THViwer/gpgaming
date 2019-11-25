package com.onepiece.treasure.games.live

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.SexyGamingClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.BetOrderUtil
import com.onepiece.treasure.games.bet.MapUtil
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class SexyGamingService: PlatformService() {

    private val currency = "MYR"

    fun startGetJson(method: String, data: List<String>): MapUtil {

        val urlParam = data.joinToString(separator = "&")
        val url = "${GameConstant.getDomain(Platform.SexyGaming)}$method?$urlParam"

        val result = okHttpUtil.doGet(url = url, clz = SexyGamingValue.Result::class.java)
        check(result.status == "0000") { OnePieceExceptionCode.PLATFORM_DATA_FAIL }

        return result.mapUtil
    }

    override fun register(registerReq: GameValue.RegisterReq): String {

        val clientToken = registerReq.token as SexyGamingClientToken

        val data = listOf(
                "cert=${clientToken.cert}",
                "agentId=${clientToken.agentId}",
                "userId=${registerReq.username}",
                "currency=$currency",
                "betLimit=" //TODO 未知
        )

        this.startGetJson(method = "/wallet/createMember", data = data)
        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as SexyGamingClientToken

        val data = listOf(
                "cert=${clientToken.cert}",
                "agentId=${clientToken.agentId}",
                "userIds=${balanceReq.username}",
                ""
        )
        val mapUtil = this.startGetJson(method = "/wallet/getBalance", data = data)

        //TODO 查看返回参数
        return mapUtil.asMap("results").asBigDecimal("clairepl")
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        val clientToken = transferReq.token as SexyGamingClientToken

        val mapUtil = when (transferReq.amount.toDouble() > 0) {
            true -> {

                val data = listOf(
                        "cert=${clientToken.cert}",
                        "agentId=${clientToken.agentId}",
                        "userId=${transferReq.username}",
                        "amount=${transferReq.amount}",
                        "txCode=${transferReq.orderId}"
                )
                this.startGetJson(method = "/wallet/deposit", data = data)
            }
            else -> {
                val data = listOf(
                        "cert=${clientToken.cert}",
                        "userId=${transferReq.username}",
                        "agentId=${clientToken.agentId}",
                        "txCode=${transferReq.orderId}",
                        "transferAmt=${transferReq.amount}"
                )
                this.startGetJson(method = "/wallet/withdraw", data = data)
            }
        }
        return mapUtil.asString("txCode")
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val clientToken = checkTransferReq.token as SexyGamingClientToken

        val data = listOf(
                "cert=${clientToken.cert}",
                "agentId=${clientToken.agentId}",
                "txCode=${checkTransferReq.orderId}"
        )
        val mapUtil = this.startGetJson(method = "/wallet/checkTransferOperation", data = data)
        return mapUtil.data["transferAmt"] != null
    }

    override fun start(startReq: GameValue.StartReq): String {
        val clientToken = startReq.token as SexyGamingClientToken

        val isMobileLogin = startReq.startPlatform == LaunchMethod.Wap
        val data = listOf(
                "cert=${clientToken.cert}",
                "agentId=${clientToken.agentId}",
                "userId=${startReq.username}",
                "isMobileLogin=$isMobileLogin",
                "externalURL=${startReq.redirectUrl}",
                ""
        )
        val mapUtil = this.startGetJson(method = "/wallet/login", data = data)

        val lang = when (startReq.language) {
            Language.EN -> "en"
            Language.CN -> "cn"
            Language.TH -> "th"
            else -> "en"
        }

        //TODO 判断语言设置启动
        val url = mapUtil.asString("url")
        return url
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val clientToken = pullBetOrderReq.token as SexyGamingClientToken

        val data = listOf(
                "cert=${clientToken.cert}",
                "agentId=${clientToken.agentId}",
                "timeFrom=${pullBetOrderReq.startTime}+08:00",
                "status=1" //已结算
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
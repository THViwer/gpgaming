package com.onepiece.gpgaming.games.live

import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.token.EvolutionClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.MapResultUtil
import com.onepiece.gpgaming.utils.RequestUtil
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class EvolutionService : PlatformService() {

    private val log = LoggerFactory.getLogger(EvolutionService::class.java)

    private fun getRequestPath(clientToken: EvolutionClientToken, path: String, data: Map<String, Any>): String {
        val params = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
        return "${clientToken.apiPath}${path}?$params"
    }

    fun doGetResult(url: String, pojo: String): Map<String, Any> {
        val result = okHttpUtil.doGet(platform = Platform.Evolution, url = url, clz = EvolutionValue.Result::class.java)
        //TODO check
        return MapResultUtil.asMap(result.data, pojo)
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val startReq = GameValue.StartReq(token = registerReq.token, username = registerReq.username, launch = LaunchMethod.Web,
                language = Language.EN, password = "-", redirectUrl = RequestUtil.getRequest().requestURI)
        this.start(startReq)

        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {

        val token = balanceReq.token as EvolutionClientToken

        val data = hashMapOf(
                "cCode" to "RWA",
                "ecID" to token.appId,
                "euID" to balanceReq.username,
                "output" to 0
        )
        val url = this.getRequestPath(clientToken = token, path = "/api/ecashier", data = data)
        val result = this.doGetResult(url, "userbalance")
        return MapResultUtil.asBigDecimal(result, "abalance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {

        val token = transferReq.token as EvolutionClientToken
        val cCode = if (transferReq.amount.toDouble() > 0) "ECR" else "EDB"

        val data = hashMapOf(
                "cCode" to cCode,
                "ecID" to token.appId,
                "euID" to transferReq.username,
                "amount" to transferReq.amount.abs(),
                "eTransID" to transferReq.orderId,
                "createuser" to "N",
                "output" to 0
        )

        val url = this.getRequestPath(clientToken = token, path = "/api/ecashier", data = data)
        val result = doGetResult(url, "transfer")
        val platformOrderId = MapResultUtil.asString(result, "etransid")
        val balance = MapResultUtil.asBigDecimal(result, "balance")
        return GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        val token = checkTransferReq.token as EvolutionClientToken

        val data = hashMapOf(
                "cCode" to "TRI",
                "ecID" to token.appId,
                "euID" to checkTransferReq.username,
                "output" to 0,
                "eTransID" to checkTransferReq.orderId
        )
        val url = this.getRequestPath(clientToken =  token, path = "/api/ecashier", data = data)
        val result = this.doGetResult(url, "transaction")
        val successful =  MapResultUtil.asString(result, "result") == "Y"
        return GameValue.TransferResp.of(successful)
    }

    override fun start(startReq: GameValue.StartReq): String {
        val token = startReq.token as EvolutionClientToken

        val ip = RequestUtil.getIpAddress()
                .let {
                    if (it.length > 15) {
                        // 如果是ip6 暂时用固定的
                        "192.168.0.1"
                    } else {
                        it
                    }
                }

        val uuid = UUID.randomUUID().toString()
        val lang = when (startReq.language) {
            Language.EN -> "en"
            Language.TH -> "th"
            Language.CN -> "zh"
            Language.ID -> "id"
            Language.MY -> "ms"
            else -> "en"
        }

        val json = """
            {
               "uuid":"$uuid",
               "player":{
                  "id":"${startReq.username}",
                  "update":true,
                  "firstName":"firstName",
                  "lastName":"lastName",
                  "nickname":"nickname",
                  "country":"${token.country}",
                  "language":"$lang",
                  "currency":"${token.currency}",
                  "session":{
                     "id":"$uuid",
                     "ip":"$ip"
                  },
                  "group": {
                     "id": "${token.betLimit}",
                     "action": "assign"
                  }
               },
               "config":{
                  "brand":{
                     "id":"1",
                     "skin":"1"
                  },
                  "channel":{
                     "wrapped":false,
                     "mobile":false
                  },
                  "urls":{
                     "cashier":"http://www.chs.ee",
                     "responsibleGaming":"http://www.RGam.ee",
                     "lobby":"http://www.lobb.ee",
                     "sessionTimeout":"http://www.sesstm.ee"
                  }
               }
            }
        """.trimIndent()

        val url = "${token.apiPath}/ua/v1/${token.appId}/${token.key}"
        val result= okHttpUtil.doPostJson(platform = Platform.Evolution, url = url, data = json, clz = EvolutionValue.Result::class.java)
        return MapResultUtil.asString(result.data, "entry")
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val token = pullBetOrderReq.token as EvolutionClientToken
        val authorization = Base64.encodeBase64String("${token.appId}:${token.key}".toByteArray())

        val utcStartTime = pullBetOrderReq.startTime.minusHours(8) // 设置UTC时间 所以要减8小时
        val utcEndTime = pullBetOrderReq.endTime.minusHours(8) // 设置UTC时间 所以要减8小时
        val url = "${token.apiOrderPath}/api/gamehistory/v1/casino/games?startDate=${utcStartTime}&endDate=${utcEndTime}"
        val headers = mapOf( "Authorization" to  "Basic $authorization")
        val jsonValue = okHttpUtil.doGet(Platform.Evolution, url, String::class.java, headers)

        if (jsonValue.contains("Data could not be found.")) return emptyList()

        val result = objectMapper.readValue<EvolutionValue.BetResult>(jsonValue)
        if (result.data.isEmpty()) return emptyList()

        return result.data.first().games.filter { MapResultUtil.asString(it.data, "status") == "Resolved" }.map {

            val games = it.data
            val settleTime = MapResultUtil.asLocalDateTime(games, "settledAt")
            val betTime = MapResultUtil.asLocalDateTime(games, "startedAt")
            val bets = MapResultUtil.asList(games, "participants")

            bets.map { bet ->

                val username = MapResultUtil.asString(bet, "playerId")
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Evolution, platformUsername = username)

                val playerBets = MapResultUtil.asList(bet, "bets")
                playerBets.map { playerBet ->
                    val code = MapResultUtil.asString(playerBet, "code")
                    val orderId = MapResultUtil.asString(playerBet, "transactionId")
                    val betAmount = MapResultUtil.asBigDecimal(playerBet, "stake")
                    val winAmount = MapResultUtil.asBigDecimal(playerBet, "payout")

                    val originData = objectMapper.writeValueAsString(bet)
                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.Evolution, orderId = "${orderId}:${code}", betTime = betTime,
                            settleTime = settleTime, betAmount = betAmount, winAmount = winAmount, originData = originData, validAmount = betAmount)
                }

            }.reduce { acc, list -> acc.plus(list) }
        }.reduce { acc, list ->
            acc.plus(list)
        }
    }


}
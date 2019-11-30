package com.onepiece.treasure.games.live

import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.token.EvolutionClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.MapResultUtil
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class EvolutionService : PlatformService() {

    private val log = LoggerFactory.getLogger(EvolutionService::class.java)

    override fun getRequestUrl(path: String, data: Map<String, Any>): String {
        val params = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
        return "${gameConstant.getDomain(Platform.Evolution)}${path}?$params"
    }

    fun doGetResult(url: String, pojo: String): Map<String, Any> {
        val result = okHttpUtil.doGet(url = url, clz = EvolutionValue.Result::class.java)
        //TODO check
        return MapResultUtil.asMap(result.data, pojo)
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val startReq = GameValue.StartReq(token = registerReq.token, username = registerReq.username, launch = LaunchMethod.Web, language = Language.EN, password = registerReq.password)
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
        val url = this.getRequestUrl(path = "/api/ecashier", data = data)
        val result = this.doGetResult(url, "userbalance")
        return MapResultUtil.asBigDecimal(result, "abalance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

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

        val url = this.getRequestUrl(path = "/api/ecashier", data = data)
        val result = doGetResult(url, "transfer")
        return MapResultUtil.asString(result, "etransid")
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val token = checkTransferReq.token as EvolutionClientToken

        val data = hashMapOf(
                "cCode" to "TRI",
                "ecID" to token.appId,
                "euID" to checkTransferReq.username,
                "output" to 0,
                "TransID" to checkTransferReq.orderId
        )
        val url = this.getRequestUrl(path = "/api/ecashier", data = data)
        val result = this.doGetResult(url, "checktransfer")
        return MapResultUtil.asString(result, "result") == "Y"
    }

    override fun start(startReq: GameValue.StartReq): String {
        val token = startReq.token as EvolutionClientToken

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
                  "country":"MY",
                  "language":"$lang",
                  "currency":"MYR",
                  "session":{
                     "id":"$uuid",
                     "ip":"192.168.0.1"
                  }
               },
               "config":{
                  "brand":{
                     "id":"1",
                     "skin":"1"
                  },
                  "game":{
                     "category":"TopGames",
                     "interface":"view1",
                     "table":{
                        "id":"leqhceumaq6qfoug"
                     }
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

        val url = "${gameConstant.getDomain(Platform.Evolution)}/ua/v1/${token.appId}/${token.key}"
        val result= okHttpUtil.doPostJson(url = url, data = json, clz = EvolutionValue.Result::class.java)
        return MapResultUtil.asString(result.data, "entry")
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val token = pullBetOrderReq.token as EvolutionClientToken
        val authorization = Base64.encodeBase64String("${token.appId}:${token.key}".toByteArray())

        val utcStartTime = pullBetOrderReq.startTime.minusHours(8) // 设置UTC时间 所以要减8小时
        val utcEndTime = pullBetOrderReq.endTime.minusHours(8) // 设置UTC时间 所以要减8小时
        val url = "${gameConstant.getDomain(Platform.Evolution)}/api/gamehistory/v1/casino/games?startDate=${utcStartTime}&endDate=${utcEndTime}"
        val headers = mapOf( "Authorization" to  "Basic $authorization")
        val jsonValue = okHttpUtil.doGet(url, String::class.java, headers)

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
                    val orderId = MapResultUtil.asString(playerBet, "transactionId")
                    val betAmount = MapResultUtil.asBigDecimal(playerBet, "stake")
                    val winAmount = MapResultUtil.asBigDecimal(playerBet, "payout")

                    val originData = objectMapper.writeValueAsString(bet)
                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.Evolution, orderId = orderId, betTime = betTime,
                            settleTime = settleTime, betAmount = betAmount, winAmount = winAmount, originData = originData)
                }

            }.reduce { acc, list -> acc.plus(list) }
        }.reduce { acc, list ->
            acc.plus(list)
        }
    }


}
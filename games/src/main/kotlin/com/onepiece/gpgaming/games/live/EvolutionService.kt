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
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import com.onepiece.gpgaming.utils.RequestUtil
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class EvolutionService : PlatformService() {

    private val log = LoggerFactory.getLogger(EvolutionService::class.java)
//
//    private fun getRequestPath(clientToken: EvolutionClientToken, path: String, data: Map<String, Any>): String {
//        val params = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
//        return "${clientToken.apiPath}${path}?$params"
//    }
//
//    fun doGetResult(url: String, pojo: String): Map<String, Any> {
//        val result = okHttpUtil.doGet(platform = Platform.Evolution, url = url, clz = EvolutionValue.Result::class.java)
//        //TODO check
//        return MapResultUtil.asMap(result.data, pojo)
//    }

    fun doGet(clientToken: EvolutionClientToken, method: String, data: Map<String, Any>): OKResponse {

        val url = "${clientToken.apiPath}${method}"
        val param = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
        val okParam = OKParam.ofGet(url = url, param = param)

        return u9HttpRequest.startRequest(okParam = okParam)
    }


    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val startReq = GameValue.StartReq(token = registerReq.token, username = registerReq.username, launch = LaunchMethod.Web,
                language = Language.EN, password = "-", redirectUrl = RequestUtil.getRequest().requestURI)
        val responseGame = this.start(startReq)
        return responseGame.copy(data = registerReq.username)
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {

        val token = balanceReq.token as EvolutionClientToken

        val data = hashMapOf(
                "cCode" to "RWA",
                "ecID" to token.appId,
                "euID" to balanceReq.username,
                "output" to 0
        )

        val okResponse = this.doGet(clientToken = token, data = data, method = "/api/ecashier")
        return this.bindGameResponse(okResponse = okResponse) {
            it.asMap("userbalance").asBigDecimal("abalance")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {

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

        val okResponse = this.doGet(clientToken = token, data = data, method = "/api/ecashier")
        return this.bindGameResponse(okResponse = okResponse) {
            val map = it.asMap("transfer")

            val platformOrderId = map.asString("etransid")
            val balance = map.asBigDecimal("balance")
            GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        val token = checkTransferReq.token as EvolutionClientToken

        val data = hashMapOf(
                "cCode" to "TRI",
                "ecID" to token.appId,
                "euID" to checkTransferReq.username,
                "output" to 0,
                "eTransID" to checkTransferReq.orderId
        )
        val okResponse = this.doGet(clientToken = token, data = data, method = "/api/ecashier")
        return this.bindGameResponse(okResponse = okResponse) {
            val map = it.asMap("transaction")
            val successful = map.asString("result") == "Y"
            GameValue.TransferResp.of(successful)
        }
    }

    override fun start(startReq: GameValue.StartReq): GameResponse<String> {
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

        val okParam = OKParam.ofPost(url = url, param = json)
        val okResponse = u9HttpRequest.startRequest(okParam)
        return this.bindGameResponse(okResponse) {
            it.asString("entry")
        }
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {
        val token = pullBetOrderReq.token as EvolutionClientToken
        val authorization = Base64.encodeBase64String("${token.appId}:${token.key}".toByteArray())

        val utcStartTime = pullBetOrderReq.startTime.minusHours(8) // 设置UTC时间 所以要减8小时
        val utcEndTime = pullBetOrderReq.endTime.minusHours(8) // 设置UTC时间 所以要减8小时
        val url = "${token.apiOrderPath}/api/gamehistory/v1/casino/games"
        val param = "startDate=${utcStartTime}&endDate=${utcEndTime}"
        val headers = mapOf("Authorization" to "Basic $authorization")

        val okParam = OKParam.ofGet(url = url, param = param, headers = headers)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)


        return this.bindGameResponse(okResponse = okResponse) {
            val content = okResponse.response
            loadData(content)
        }

    }

    fun loadData(content: String): List<BetOrderValue.BetOrderCo> {
        if (content.contains("Data could not be found.")) return emptyList()

        val result = objectMapper.readValue<EvolutionValue.BetResult>(content)
        if (result.data.isEmpty()) return emptyList()

        return result.data.first().games.filter { MapResultUtil.asString(it.data, "status") == "Resolved" }.map {

            val games = it.data
            val settleTime = MapResultUtil.asLocalDateTime(games, "settledAt")
                    .plusHours(8) // 需要+8小时
            val betTime = MapResultUtil.asLocalDateTime(games, "startedAt")
                    .plusHours(8) // 需要+8小时
            val bets = MapResultUtil.asList(games, "participants")

            val tableId = try {
                MapResultUtil.asMap(games, "table")["id"]?.toString() ?: ""
            } catch (e: Exception) {
                ""
            }
            val id= MapResultUtil.asString(games, "id")

            bets.map { bet ->

                val username = MapResultUtil.asString(bet, "playerId")
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Evolution, platformUsername = username)

                val playerBets = MapResultUtil.asList(bet, "bets")


                playerBets.map { playerBet ->
                    val code = MapResultUtil.asString(playerBet, "code")
                    val orderId = MapResultUtil.asString(playerBet, "transactionId")
                    val betAmount = MapResultUtil.asBigDecimal(playerBet, "stake")
                    val payout = MapResultUtil.asBigDecimal(playerBet, "payout")

                    // 如果是轮盘 有效打码为0
                    val validAmount = when (tableId) {
                        "lkcbrbdckjxajdol", "7x0b1tgh7agmf6hv", "AmericanTable001", "vctlz20yfnmp1ylr",
                            "wzg6kdkad1oe7m5k", "48z5pjps3ntvqc1b", "SpeedAutoRo00001", "01rb77cq1gtenhmo",
                            "f1f4rm9xgh4j3u2z", "LightningTable01", "InstantRo0000001" -> BigDecimal.ZERO
                        else -> betAmount
                    }

                    val originData = objectMapper.writeValueAsString(bet)
                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.Evolution, orderId = "${id}:${orderId}:${code}", betTime = betTime,
                            settleTime = settleTime, betAmount = betAmount, payout = payout, originData = originData, validAmount = validAmount)
                }

            }.reduce { acc, list -> acc.plus(list) }
        }.reduce { acc, list ->
            acc.plus(list)
        }
    }


}
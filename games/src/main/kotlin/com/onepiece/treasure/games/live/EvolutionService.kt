package com.onepiece.treasure.games.live

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.beans.model.token.EvolutionClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.bet.MapResultUtil
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class EvolutionService : PlatformApi() {

    private val log = LoggerFactory.getLogger(EvolutionService::class.java)


    override fun getRequestUrl(path: String, data: Map<String, Any>): String {
        val params = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
        return "${GameConstant.EVOLUTION_API_URL}${path}?$params"
    }

    fun doGetResult(url: String): EvolutionValue.Result {
        val result = okHttpUtil.doGet(url = url, clz = EvolutionValue.Result::class.java)
        //TODO check
        return result
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val startReq = GameValue.StartReq(token = registerReq.token, username = registerReq.username, startPlatform = LaunchMethod.Web, language = Language.EN)
        this.start(startReq)

        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {

        val token = balanceReq.token as DefaultClientToken

        val data = hashMapOf(
                "cCode" to "RWA",
                "ecID" to token.appId,
                "euID" to balanceReq.username,
                "output" to 0
        )
        val url = this.getRequestUrl(path = "/api/ecashier", data = data)
        val result = this.doGetResult(url)
        return MapResultUtil.toBigDecimal(result.data, "abalance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

        val token = transferReq.token as DefaultClientToken
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
        val result = doGetResult(url)
        return MapResultUtil.asString(result.data, "etransid")
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val token = checkTransferReq.token as DefaultClientToken

        val data = hashMapOf(
                "cCode" to "TRI",
                "ecID" to token.appId,
                "euID" to checkTransferReq.username,
                "output" to 0,
                "TransID" to checkTransferReq.orderId
        )
        val url = this.getRequestUrl(path = "/api/ecashier", data = data)
        val result = this.doGetResult(url)
        return MapResultUtil.asString(result.data, "result") == "Y"
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

        val url = "${GameConstant.EVOLUTION_API_URL}/ua/v1/${token.appId}/${token.key}"
        val result= okHttpUtil.doPostJson(url = url, data = json, clz = EvolutionValue.Result::class.java)
        return MapResultUtil.asString(result.data, "entry")
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val token = pullBetOrderReq.token as EvolutionClientToken
        val authorization = DigestUtils.md5Hex("key-${token.key}:${token.key}")

        val url = "${GameConstant.EVOLUTION_API_URL}/api/gamehistory/v1/casino/games/stream?startDate=${pullBetOrderReq.startTime}"
        val data = okHttpUtil.doGet(url, String::class.java, "Basic $authorization")

        return emptyList()

    }


}
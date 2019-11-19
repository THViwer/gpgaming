package com.onepiece.treasure.games.live.evolution

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.http.OkHttpUtil
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class EvolutionService(
        private val okHttpUtil: OkHttpUtil
) : PlatformApi() {

    private val log = LoggerFactory.getLogger(EvolutionService::class.java)

    override fun register(registerReq: GameValue.RegisterReq): String {
        // EvoConfig.API_URL+"/ua/v1/"+EvoConfig.KEY+"/"+EvoConfig.TOKEN;

        val startReq = GameValue.StartReq(token = registerReq.token, username = registerReq.username, startPlatform = LaunchMethod.Web, language = Language.EN)
        this.start(startReq)

        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {

        val url = EvolutionBuild.instance(token = balanceReq.token as DefaultClientToken, cCode = "RWA", username = balanceReq.username)
                .set("output", "0")
                .build(path = "/api/ecashier")

        val result = okHttpUtil.doGet(url, EvolutionValue.BalanceResult::class.java)

        log.info("getBalance: $result")
        return result.userbalance.abalance
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

        val cCode = if (transferReq.amount.toDouble() > 0) "ECR" else "EDB"

        val url = EvolutionBuild.instance(token = transferReq.token as DefaultClientToken, cCode = cCode, username = transferReq.username)
                .set("amount", transferReq.amount.abs())
                .set("eTransID", transferReq.orderId)
                .set("createuser", "N")
                .set("output", "0")
                .build(path = "/api/ecashier")

        val result = okHttpUtil.doGet(url, EvolutionValue.TransferResult::class.java)
        log.info("deposit or withdraw result: $result")

        return transferReq.orderId
    }

    override fun start(startReq: GameValue.StartReq): String {
        val token = startReq.token as DefaultClientToken

        val uuid = UUID.randomUUID().toString()
//        val session = EvolutionValue.Player.Session(id = uuid, ip = "241.13.291.1")
//        val player = EvolutionValue.Player(id = startReq.username, session = session)
//        val config = EvolutionValue.Config()
//        val registerPlayer = EvolutionValue.RegisterPlayer(uuid = uuid, player = player, config = config)


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
        val result= okHttpUtil.doPostJson(url = url, data = json, clz = EvolutionValue.GetUrlOrCreateUser::class.java)

        return result.entry
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val token = pullBetOrderReq.token as DefaultClientToken
        val authorization = DigestUtils.md5Hex("${token.appId}:${token.key}")

        val url = "${GameConstant.EVOLUTION_API_URL}/api/gamehistory/v1/casino/games/stream?startDate=${pullBetOrderReq.startTime}"

        //TODO 还未调试完成
        okHttpUtil.doGet(url, String::class.java, "Basic $authorization")


        return emptyList()

    }


}
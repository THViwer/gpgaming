package com.onepiece.treasure.games.live.evolution

import com.onepiece.treasure.beans.enums.StartPlatform
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.http.OkHttpUtil
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

        val startReq = GameValue.StartReq(token = registerReq.token, username = registerReq.username, startPlatform = StartPlatform.Pc)
        this.start(startReq)

        return registerReq.username
    }



    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {

        val url = EvolutionBuild.instance(token = balanceReq.token as DefaultClientToken, cCode = "RWA", username = balanceReq.username)
                .set("output", "1")
                .build(path = "/api/ecashier")

        val result = okHttpUtil.doGet(url, String::class.java)

        log.info("getBalance: $result")
        return BigDecimal.ZERO
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

        val url = EvolutionBuild.instance(token = transferReq.token as DefaultClientToken, cCode = "TRI", username = transferReq.username)
                .set("eTransID", transferReq.orderId)
                .set("output", "1")
                .build(path = "/api/ecashier")

        val result = okHttpUtil.doGet(url, String::class.java)
        log.info("transfer info : $url")

        return transferReq.orderId
    }

    override fun start(startReq: GameValue.StartReq): String {
        val token = startReq.token as DefaultClientToken

        val uuid = UUID.randomUUID().toString()
        val session = EvolutionValue.Player.Session(id = uuid, ip = "241.13.291.1")
        val player = EvolutionValue.Player(id = startReq.username, session = session)

        val config = EvolutionValue.Config()

        val registerPlayer = EvolutionValue.RegisterPlayer(uuid = uuid, player = player, config = config)


        val json = """
            { 
               "uuid":"$uuid",
               "player":{ 
                  "id":"$uuid",
                  "update":true,
                  "firstName":"firstName",
                  "lastName":"lastName",
                  "nickname":"nickname",
                  "country":"MY",
                  "language":"en",
                  "currency":"MYR",
                  "session":{ 
                     "id":"${startReq.username}",
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
        log.info("result error message: $result")

        return result.entry
    }
}
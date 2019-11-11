//package com.onepiece.treasure.games.live.evolution
//
//import com.onepiece.treasure.beans.model.token.DefaultClientToken
//import com.onepiece.treasure.games.GameConstant
//import com.onepiece.treasure.games.http.OkHttpUtil
//import org.slf4j.LoggerFactory
//import org.springframework.stereotype.Service
//import java.math.BigDecimal
//import java.util.*
//
//@Service
//class EvolutionApiService(
//        private val okHttpUtil: OkHttpUtil
//) : EvolutionApi {
//
//    private val log = LoggerFactory.getLogger(EvolutionApiService::class.java)
//
//    override fun createUser(token: DefaultClientToken, username: String): String {
//
//        // EvoConfig.API_URL+"/ua/v1/"+EvoConfig.KEY+"/"+EvoConfig.TOKEN;
//
//        val uuid = UUID.randomUUID().toString()
//        val session = EvolutionValue.Player.Session(id = uuid, ip = "241.13.291.1")
//        val player = EvolutionValue.Player(id = username, session = session)
//
//        val config = EvolutionValue.Config()
//
//        val registerPlayer = EvolutionValue.RegisterPlayer(uuid = uuid, player = player, config = config)
//
//
//        val url = "${GameConstant.EVOLUTION_API_URL}/ua/v1/${token.appId}/${token.key}"
//        val result= okHttpUtil.doPostJson(url = url, data = registerPlayer, clz = EvolutionValue.GetUrlOrCreateUser::class.java)
//        log.info("result error message: $result")
//
//        return username
//    }
//
//
//    override fun depositOrWithdraw(token: DefaultClientToken, username: String, orderId: String, amount: BigDecimal) {
//        val cCode = if (amount.toDouble() > 0) "ECR" else "EDB"
//
//        val url = EvolutionBuild.instance(token = token, cCode = cCode, username = username)
//                .set("amount", amount)
//                .set("eTransID", amount)
//                .set("createuser", "N")
//                .set("output", "1")
//                .build(path = "/api/ecashier")
//
//        val result = okHttpUtil.doGet(url, String::class.java)
//        log.info("deposit or withdraw result: $result")
//
//    }
//
//    override fun getBalance(token: DefaultClientToken, username: String): BigDecimal {
//
//        val url = EvolutionBuild.instance(token = token, cCode = "RWA", username = username)
//                .set("output", "1")
//                .build(path = "/api/ecashier")
//
//        val result = okHttpUtil.doGet(url, String::class.java)
//
//        log.info("getBalance: $result")
//        return BigDecimal.ZERO
//    }
//
//    override fun transactionInfo(token: DefaultClientToken, orderId: String, username: String): Boolean {
//
//        val url = EvolutionBuild.instance(token = token, cCode = "TRI", username = username)
//                .set("eTransID", orderId)
//                .set("output", "1")
//                .build(path = "/api/ecashier")
//
//        val result = okHttpUtil.doGet(url, String::class.java)
//        log.info("transfer info : $url")
//        return true
//    }
//}
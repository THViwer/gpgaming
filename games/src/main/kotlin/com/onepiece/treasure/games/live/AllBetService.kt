package com.onepiece.treasure.games.live

import com.onepiece.treasure.beans.model.token.AllBetClientToken
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.bet.MapUtil
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class AllBetService : PlatformApi() {


    fun startDoGet(method: String, data: Map<String, Any>): MapUtil {

        val param = data.map { "${it.key}=${it.value}" }.joinToString(separator = ",")

        val result = okHttpUtil.doGet(url = "${GameConstant.ALLBET_API_URL}/check_or_create?$param", clz = AllBetValue.Result::class.java)
        return result.mapUtil
    }

    private fun queryHandicap(allBetClientToken: AllBetClientToken) {

        val data = hashMapOf(
                "random" to UUID.randomUUID().toString(),
                "agent" to allBetClientToken.agentName
        )

        val mapUtil = this.startDoGet(method = "/query_handicap", data = data)
        val handicaps = mapUtil.asList("handicaps")
        println(handicaps)
    }

    override fun register(registerReq: GameValue.RegisterReq): Pair<String, String> {
        val allBetClientToken = registerReq.token as AllBetClientToken

        // 查询盘口
        this.queryHandicap(allBetClientToken)

        val random = UUID.randomUUID().toString()

        val data = mapOf(
                "random" to random,
                "agent" to allBetClientToken.agentName,
                "password" to registerReq.password,
                "orHandicapNames" to "1"
        )


        error("")
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val allBetCientToken = balanceReq.token as AllBetClientToken


        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        val allBetCientToken = transferReq.token as AllBetClientToken

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
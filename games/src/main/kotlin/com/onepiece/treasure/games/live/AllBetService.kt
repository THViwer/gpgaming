package com.onepiece.treasure.games.live

import com.onepiece.treasure.beans.model.token.AllBetClientToken
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.bet.DesUtil
import com.onepiece.treasure.games.bet.MapUtil
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class AllBetService : PlatformApi() {

    private val log = LoggerFactory.getLogger(AllBetService::class.java)


    fun startDoGet(method: String, urlParam: String, allBetClientToken: AllBetClientToken): MapUtil {

        val desData =  DesUtil.encrypt(urlParam, allBetClientToken.desKey, null)
        val md5Data = Base64.encodeBase64String(DigestUtils.md5("$desData${allBetClientToken.md5Key}"))
        val param = "propertyId=${allBetClientToken.propertyId}&data=$desData&sign=$md5Data&${urlParam}"

        val result = okHttpUtil.doGet(url = "${GameConstant.ALLBET_API_URL}${method}?$param", clz = AllBetValue.Result::class.java)
        return result.mapUtil
    }

    private fun queryHandicap(allBetClientToken: AllBetClientToken) {

        val urlParam = "agent=${allBetClientToken.agentName}&random=${UUID.randomUUID()}"

        val mapUtil = this.startDoGet(method = "/query_handicap", urlParam = urlParam, allBetClientToken = allBetClientToken)
        val handicaps = mapUtil.asList("handicaps")
        println(handicaps)
    }

    override fun register(registerReq: GameValue.RegisterReq): String {
        val allBetClientToken = registerReq.token as AllBetClientToken

        // 查询盘口
        this.queryHandicap(allBetClientToken)

        val data = listOf(
                "agent=${allBetClientToken.agentName}",
                "random=${UUID.randomUUID()}",
                "client=${registerReq.username}",
                "password=${registerReq.password}",
                "orHandicapNames=A",
                "vipHandicapNames=VIP_0",
                "orHallRebate=0"
        )
        val urlParam = data.joinToString(separator = "&")

        val mapUtil = this.startDoGet(method = "/check_or_create", urlParam = urlParam, allBetClientToken = allBetClientToken )
        return mapUtil.asString("client")
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
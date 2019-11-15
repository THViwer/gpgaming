package com.onepiece.treasure.games.live.ct

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.order.BetCacheVo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.core.order.CTBetOrder
import com.onepiece.treasure.core.order.CTBetOrderDao
import com.onepiece.treasure.utils.RedisService
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class CTApiService(
        private val okHttpUtil: OkHttpUtil,
        private val redisService: RedisService,
        private val ctBetOrderDao: CTBetOrderDao
) : CTApi {

    // 暂时用马币
    val currency = "MYR"
    val lang = "en"


    fun checkCode(codeId: Int) {
        when (codeId) {
            0 -> {}
            300 -> { OnePieceExceptionCode.PLATFORM_AEGIS}
            else -> { OnePieceExceptionCode.PLATFORM_REQUEST_ERROR }
        }
    }

    override fun signup(token: DefaultClientToken, username: String, password: String): String {

        val param = CTBuild.instance(token, "signup")

        val md5Password = DigestUtils.md5Hex(password)
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "data":"G",
                "member":{
                    "username":"$username",
                    "password":"$md5Password",
                    "currencyName":"$currency",
                    "winLimit":1000
                }
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, CTValue.SignupResult::class.java)
        checkCode(result.codeId)

        return username
    }

    override fun login(token: DefaultClientToken, username: String, startPlatform: LaunchMethod): String {

        val param = CTBuild.instance(token, "login")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "lang":"$lang",
                "member":{
                    "username":"$username"
                }
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, CTValue.LoginResult::class.java)
        checkCode(result.codeId)

        return when (startPlatform) {
            LaunchMethod.Web -> result.list[0]
            LaunchMethod.Wap -> result.list[1]
            else -> result.list[2]
        }.plus(result.token)

    }

    override fun loginFree(token: DefaultClientToken, startPlatform: LaunchMethod): String {
        val param = CTBuild.instance(token, "free")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "lang":"$lang",
                "device": 1
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, CTValue.LoginResult::class.java)
        checkCode(result.codeId)

        return when (startPlatform) {
            LaunchMethod.Web -> result.list[0]
            LaunchMethod.Wap -> result.list[1]
            else -> result.list[2]
        }.plus(result.token)
    }

    override fun getBalance(token: DefaultClientToken, username: String): BigDecimal {

        val param = CTBuild.instance(token,"getBalance")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "member":{"username":"$username"}
            } 
        """.trimIndent()
        val result = okHttpUtil.doPostJson(param.url, data, CTValue.BalanceResult::class.java)
        return result.member.balance
    }

    override fun transfer(token: DefaultClientToken, username: String, orderId: String, amount: BigDecimal): String {
        val param = CTBuild.instance(token, "transfer")

        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "data":"$orderId",
                "member":{
                    "username":"$username",
                    "amount":${amount}
                }
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, CTValue.Transfer::class.java)
        return result.data
    }

    override fun checkTransfer(token: DefaultClientToken, orderId: String): Boolean {

        val param = CTBuild.instance(token, "transfer")

        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "data":"${orderId}"
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, CTValue.CheckTransferResult::class.java)
        return result.codeId == 0
    }

    override fun getReport(token: DefaultClientToken): String {
        val processId = UUID.randomUUID().toString().replace("-", "")

        val param = CTBuild.instance(token = token, method = "getReport")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}"
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, CTValue.Report::class.java)
        checkCode(result.codeId)

        if (result.list == null) return processId


        val now = LocalDateTime.now()
        val orders = result.list.map {

            val username = it.userName
            val clientId = username.substring(1, 4).toInt()
            val memberId = username.substring(4, username.length).toInt()
            with(it) {
                CTBetOrder(id = id, clientId = clientId, memberId = memberId, lobbyId = lobbyId, platformMemberId = it.memberId, shoeId = shoeId,
                        tableId = tableId, playId = playId, gameId = gameId, gameType = gameType, betTime = betTime, calTime = calTime, winOrLoss = winOrLoss,
                        winOrLossz = winOrLossz, betPointsz = betPointsz, betPoints = betPoints, betDetailz = betDetailz, betDetail = betDetail,
                        balanceBefore = balanceBefore, parentBetId = parentBetId, availableBet = availableBet, ip = ip, ext = ext, isRevocation = isRevocation,
                        currencyId = currencyId, deviceType = deviceType, pluginId = pluginId, result = it.result, userName = userName, createdTime = now)
            }
        }
        // 存储订单
        ctBetOrderDao.create(orders)

        // 放到缓存
        val caches = orders.groupBy { it.memberId }.map {
            val memberId = it.key
            val money = it.value.sumByDouble { it.betPoints.toDouble() }.toBigDecimal().setScale(2, 2)

            BetCacheVo(memberId = memberId, bet = money, platform = Platform.CT)
        }
        val redisKey = OnePieceRedisKeyConstant.betCache(processId)
        redisService.put(redisKey, caches)

        // 过滤已结算的
        val ids = result.list.filter { it.isRevocation == 1 }.map { it.id }
        this.mark(token = token, ids = ids)

        return processId
    }

    private fun mark(token: DefaultClientToken, ids: List<Long>) {

        val list = ids.joinToString(separator = ",")
        val param = CTBuild.instance(token = token, method = "mark")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "list":[$list]
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, CTValue.Mark::class.java)
        checkCode(result.codeId)

    }
}
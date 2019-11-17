package com.onepiece.treasure.games.live.dg

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.ClientToken
import com.onepiece.treasure.beans.value.order.BetCacheVo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.order.CTBetOrder
import com.onepiece.treasure.core.order.DGBetOrderDao
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.utils.RedisService
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class DgService(
        private val okHttpUtil: OkHttpUtil,
        private val redisService: RedisService,
        private val dgBetOrderDao: DGBetOrderDao
) : PlatformApi() {

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


    override fun register(registerReq: GameValue.RegisterReq): String {

        val param = DGBuild.instance(registerReq.token, "/user/signup")

        val md5Password = DigestUtils.md5Hex(registerReq.password)
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "data":"G",
                "member":{
                    "username":"${registerReq.username}",
                    "password":"$md5Password",
                    "currencyName":"$currency",
                    "winLimit":1000
                }
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, DGValue.SignupResult::class.java)
        checkCode(result.codeId)

        return registerReq.username

    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {

        val token = balanceReq.token
        val username = balanceReq.username

        val param = DGBuild.instance(token,"/user/getBalance")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "member":{"username":"$username"}
            } 
        """.trimIndent()
        val result = okHttpUtil.doPostJson(param.url, data, DGValue.BalanceResult::class.java)
        checkCode(result.codeId)
        return result.member.balance
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

        val token = transferReq.token

        val param = DGBuild.instance(token, "/account/transfer")

        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "data":"${transferReq.orderId}",
                "member":{
                    "username":"${transferReq.username}",
                    "amount":${transferReq.amount}
                }
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, DGValue.Transfer::class.java)
        checkCode(result.codeId)
        return result.data

    }


    override fun start(startReq: GameValue.StartReq): String {

        val param = DGBuild.instance(startReq.token, "/user/login")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "lang":"$lang",
                "member":{
                    "username":"${startReq.username}"
                }
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, DGValue.LoginResult::class.java)
        checkCode(result.codeId)

        return when (startReq.startPlatform) {
            LaunchMethod.Web -> result.list[0]
            LaunchMethod.Wap -> result.list[1]
            else -> result.list[2]
        }.plus(result.token)

    }


    override fun startSlotDemo(token: ClientToken, startPlatform: LaunchMethod): String {
        val param = DGBuild.instance(token, "/user/free")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "lang":"$lang",
                "device": 1
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, DGValue.LoginResult::class.java)
        checkCode(result.codeId)

        return when (startPlatform) {
            LaunchMethod.Web -> result.list[0]
            LaunchMethod.Wap -> result.list[1]
            else -> result.list[2]
        }.plus(result.token)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val param = DGBuild.instance(checkTransferReq.token, "/account/transfer")

        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "data":"${checkTransferReq.orderId}"
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, DGValue.CheckTransferResult::class.java)
        return result.codeId == 0
    }


    override fun asynBetOrder(syncBetOrderReq: GameValue.SyncBetOrderReq): String {

        val token = syncBetOrderReq.token

        val processId = UUID.randomUUID().toString().replace("-", "")

        val param = DGBuild.instance(token = token, method = "/game/getReport")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}"
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, DGValue.Report::class.java)
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
        dgBetOrderDao.create(orders)

        // 放到缓存
        val caches = orders.groupBy { it.memberId }.map {
            val memberId = it.key
            val money = it.value.sumByDouble { it.betPoints.toDouble() }.toBigDecimal().setScale(2, 2)

            BetCacheVo(memberId = memberId, bet = money, platform = Platform.DG)
        }
        val redisKey = OnePieceRedisKeyConstant.betCache(processId)
        redisService.put(redisKey, caches)

        // 过滤已结算的
        val ids = result.list.filter { it.isRevocation == 1 }.map { it.id }
        this.mark(token = token, ids = ids)

        return processId


    }

    private fun mark(token: ClientToken, ids: List<Long>) {

        val list = ids.joinToString(separator = ",")
        val param = DGBuild.instance(token = token, method = "/game/markReport")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "list":[$list]
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, DGValue.Mark::class.java)
        checkCode(result.codeId)

    }
}
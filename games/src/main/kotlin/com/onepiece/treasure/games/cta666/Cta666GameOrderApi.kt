package com.onepiece.treasure.games.cta666

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.order.BetCacheVo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.order.BetOrderValue
import com.onepiece.treasure.core.order.Cta666BetOrder
import com.onepiece.treasure.core.order.Cta666BetOrderDao
import com.onepiece.treasure.games.GameOrderApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class Cta666GameOrderApi(
        private val okHttpUtil: OkHttpUtil,
        private val cta666BetOrderDao: Cta666BetOrderDao,
        private val redisService: RedisService
): GameOrderApi {

    override fun synOrder(startTime: LocalDateTime, endTime: LocalDateTime): String {

        val processId = UUID.randomUUID().toString().replace("-", "")

        val param = Cat666ParamBuilder.instance("getReport")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}"
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, Cta666Result.Report::class.java)
        Cat666Constant.checkCode(result.codeId)

        if (result.list == null) return processId


        val now = LocalDateTime.now()
        val orders = result.list.map {

            val username = it.userName
            val clientId = username.substring(1, 4).toInt()
            val memberId = username.substring(4, username.length).toInt()
            with(it) {
                Cta666BetOrder(id = id, clientId = clientId, memberId = memberId, lobbyId = lobbyId, platformMemberId = it.memberId, shoeId = shoeId,
                        tableId = tableId, playId = playId, gameId = gameId, gameType = gameType, betTime = betTime, calTime = calTime, winOrLoss = winOrLoss,
                        winOrLossz = winOrLossz, betPointsz = betPointsz, betPoints = betPoints, betDetailz = betDetailz, betDetail = betDetail,
                        balanceBefore = balanceBefore, parentBetId = parentBetId, availableBet = availableBet, ip = ip, ext = ext, isRevocation = isRevocation,
                        currencyId = currencyId, deviceType = deviceType, pluginId = pluginId, result = it.result, userName = userName, createdTime = now)
            }
        }
        cta666BetOrderDao.create(orders)

        // 放到缓存
        val caches = orders.groupBy { it.memberId }.map {
            val memberId = it.key
            val money = it.value.sumByDouble { it.betPoints.toDouble() }.toBigDecimal().setScale(2, 2)

            BetCacheVo(memberId = memberId, bet = money, platform = Platform.Cta666)
        }
        val redisKey = OnePieceRedisKeyConstant.betCache(processId)
        redisService.put(redisKey, caches)

        // 过滤已结算的
        val ids = result.list.filter { it.isRevocation == 1 }.map { it.id }
        this.mark(ids)

        return processId
    }

    private fun mark(ids: List<Long>) {

        val list = ids.joinToString(separator = ",")
        val param = Cat666ParamBuilder.instance("mark")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "list":[$list]
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, Cta666Result.Mark::class.java)
        Cat666Constant.checkCode(result.codeId)

    }

    override fun report(startDate: LocalDate, endDate: LocalDate): List<BetOrderValue.Report> {
        return cta666BetOrderDao.report(startDate = startDate, endDate = endDate)
    }
}
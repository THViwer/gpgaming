package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.BetOrder
import com.onepiece.gpgaming.beans.value.database.BetOrderReport
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.BetOrderDao
import com.onepiece.gpgaming.core.service.BetOrderService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class BetOrderServiceImpl(
        private val betOrderDao: BetOrderDao,
        private val redisService: RedisService
) : BetOrderService {

    override fun batch(orders: List<BetOrderValue.BetOrderCo>) {
        betOrderDao.batch(orders)
    }

    override fun getBets(clientId: Int, memberId: Int, platform: Platform): List<BetOrder> {
        return betOrderDao.getBets(clientId, memberId, platform)
    }

    override fun last500(clientId: Int, memberId: Int, startDate: LocalDate, endDate: LocalDate): List<BetOrder> {
        return betOrderDao.last500(clientId, memberId, startDate, endDate)
    }

    override fun getNotMarkBets(tableSequence: Int): List<BetOrderValue.BetMarkVo> {

        val table = "bet_order_$tableSequence"

        // 查询最后一个标记的key是什么
        val lastMarkIdKey = OnePieceRedisKeyConstant.getLastMarkBetId(table)
        val lastMarkId = redisService.get(lastMarkIdKey, Int::class.java) {
            betOrderDao.getLastNotMarkId(table)
        } ?: 0

        // 查询未被标记的数据
        val orders = betOrderDao.getNotMarkBets(table = table, startId = lastMarkId)
        if (orders.isEmpty()) return emptyList()

        val endId = orders.last().id

        // 合并数据
        val mergeOrders = orders.groupBy { "${it.clientId}:${it.memberId}:${it.platform}" }.map { maps ->

            when (maps.value.size == 1) {
                true -> maps.value.first()
                else -> {
                    val betAmount = maps.value.sumByDouble { it.betAmount.toDouble() }.toBigDecimal().setScale(2, 2)
                    val winAmount = maps.value.sumByDouble { it.winAmount.toDouble() }.toBigDecimal().setScale(2, 2)

                    maps.value.first().copy(id = -1, betAmount = betAmount, winAmount = winAmount)
                }
            }
        }

        // 标记已处理的订单
        betOrderDao.markBet(table = table, startId = lastMarkId, endId = endId)

        return mergeOrders
    }

    override fun report(memberId: Int?, startDate: LocalDate, endDate: LocalDate): List<BetOrderReport> {
        return betOrderDao.report(memberId = memberId, startDate = startDate, endDate = endDate)
    }

    override fun getTotalBet(clientId: Int, memberId: Int, startDate: LocalDate): BigDecimal {
        return betOrderDao.getTotalBet(clientId = clientId, memberId = memberId, startDate = startDate)
    }
}
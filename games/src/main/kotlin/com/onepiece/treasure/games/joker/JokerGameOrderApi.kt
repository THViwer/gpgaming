package com.onepiece.treasure.games.joker

import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.order.JokerBetOrder
import com.onepiece.treasure.core.order.JokerBetOrderDao
import com.onepiece.treasure.games.GameOrderApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.joker.value.BetResult
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class JokerGameOrderApi(
        private val jokerBetOrderDao: JokerBetOrderDao,
        private val okHttpUtil: OkHttpUtil,
        private val redisService: RedisService
) : GameOrderApi {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm")

    override fun synOrder(startDate: LocalDate, endDate: LocalDate): String {

        val nextId = redisService.get(OnePieceRedisKeyConstant.jokerNextId(), String::class.java) {
            //TODO 从数据库中查询
            UUID.randomUUID().toString()
        }!!

        val urlParam = JokerParamBuilder.instance("TS")
                .set("StartDate", startDate.format(dateFormatter))
                .set("EndDate", endDate.format(dateFormatter))
                .set("NextId", nextId)
                .build()

        val betResult = okHttpUtil.doPost(JokerConstant.url, urlParam, BetResult::class.java)

        val orders = betResult.data.getValue("Game").map {
            val username = it.username
            val clientId = username.substring(0, 2).toInt()
            val memberId = username.substring(2, username.length).toInt()
            val now = LocalDateTime.now()

            JokerBetOrder(oCode = it.oCode, clientId = clientId, memberId = memberId, gameCode = it.gameCode, description = it.description,
                    type = it.type, amount = it.Amount, result = it.result, time = it.time, appId = it.appId, createdTime = now, username = username)
        }

        jokerBetOrderDao.creates(orders)

        // 放到缓存
        val caches = orders.groupBy { it.memberId }.map {
            val memberId = it.key
            val money = it.value.sumByDouble { it.amount.toDouble() }.toBigDecimal().setScale(2, 2)
            "${memberId}_$money"
        }
        val redisKey = OnePieceRedisKeyConstant.betCache(nextId)
        redisService.put(redisKey, caches)

        // 下一次的值set到redis中
        redisService.put(OnePieceRedisKeyConstant.jokerNextId(), betResult.nextId)

        return nextId
    }
}
package com.onepiece.treasure.games.joker

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.order.BetCacheVo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.order.JokerBetOrder
import com.onepiece.treasure.core.order.JokerBetOrderDao
import com.onepiece.treasure.games.GameOrderApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.joker.value.BetResult
import com.onepiece.treasure.utils.RedisService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class JokerGameOrderApi(
        private val jokerBetOrderDao: JokerBetOrderDao,
        private val okHttpUtil: OkHttpUtil,
        private val redisService: RedisService
) : GameOrderApi {

    private val log = LoggerFactory.getLogger(JokerGameCashApi::class.java)

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun synOrder(startTime: LocalDateTime, endTime: LocalDateTime): String {

        val nextId = redisService.get(OnePieceRedisKeyConstant.jokerNextId(), String::class.java) {
            //TODO 从数据库中查询
            UUID.randomUUID().toString().replace("-", "")
        }!!

        val (url, formBody) = JokerParamBuilder.instance("TS")
                .set("StartDate", startTime.format(dateFormatter))
                .set("EndDate", endTime.format(dateFormatter))
                .set("NextId", nextId)
                .build()

        log.info("StartDate=${startTime.format(dateFormatter)}&EndDate=${endTime.format(dateFormatter)}")

        val betResult = okHttpUtil.doPostForm(url, formBody, BetResult::class.java)

        val orders = betResult.data["Game"]?.map {
            val username= it.username
            val clientId = username.substring(0, 3).toInt()
            val memberId = username.substring(3, username.length).toInt()
            val now = LocalDateTime.now()

            JokerBetOrder(oCode = it.oCode, clientId = clientId, memberId = memberId, gameCode = it.gameCode, description = it.description,
                    type = it.type, amount = it.amount, result = it.result, time = it.time.toLocalDateTime(), appId = it.appId, createdTime = now,
                    username = username, currencyCode = it.currencyCode, details = it.details, freeAmount = it.freeAmount, roundId = it.roundId)
        }

        if (orders != null) {
            jokerBetOrderDao.creates(orders)
            // 放到缓存
            val caches = orders.groupBy { it.memberId }.map {
                val memberId = it.key
                val money = it.value.sumByDouble { it.amount.toDouble() }.toBigDecimal().setScale(2, 2)

                BetCacheVo(memberId = memberId, bet = money, platform = Platform.Joker)
            }
            val redisKey = OnePieceRedisKeyConstant.betCache("")
            redisService.put(redisKey, caches)

            // 下一次的值set到redis中
            redisService.put(OnePieceRedisKeyConstant.jokerNextId(), betResult.nextId)
        }

        return if (betResult.nextId.isNotBlank()) betResult.nextId else nextId
    }
}
package com.onepiece.gpgaming.core

import com.onepiece.gpgaming.utils.RedisService
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class MarketUtil(
        private val redisService: RedisService
) {

    fun addRV(clientId: Int, marketId:  Int) {
        val redisKey = "market:rv:$clientId:$marketId:${LocalDate.now()}"
        redisService.increase(key = redisKey, timeout = 86400 * 2)
    }

    fun addPV(clientId: Int, marketId: Int) {
        val redisKey = "market:pv:$clientId:$marketId:${LocalDate.now()}"
        redisService.increase(key = redisKey, timeout = 86400 * 2)
    }

    fun getRV(clientId: Int, marketId:  Int, day: LocalDate): Int {
        val redisKey = "market:rv:$clientId:$marketId:${LocalDate.now()}"
        return redisService.get(redisKey, Int::class.java) ?: 0
    }

    fun getPV(clientId: Int, marketId:  Int, day: LocalDate): Int {
        val redisKey = "market:pv:$clientId:$marketId:${LocalDate.now()}"
        return redisService.get(redisKey, Int::class.java) ?: 0
    }

}
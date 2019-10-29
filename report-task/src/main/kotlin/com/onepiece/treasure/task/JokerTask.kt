package com.onepiece.treasure.task

import com.onepiece.treasure.core.order.JokerBetOrderDao
import com.onepiece.treasure.games.GameOrderApi
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class JokerTask(
        private val jokerOrderApi: GameOrderApi,
        private val jokerBetOrderDao: JokerBetOrderDao,
        private val betCacheUtil: BetCacheUtil
) {

    fun syncOrder() {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(1)
        val unionId = jokerOrderApi.synOrder(startDate = startDate, endDate = endDate)

        betCacheUtil.handler(unionId)

    }



}
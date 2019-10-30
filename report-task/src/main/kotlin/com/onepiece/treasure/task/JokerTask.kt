package com.onepiece.treasure.task

import com.onepiece.treasure.core.order.JokerBetOrderDao
import com.onepiece.treasure.games.GameOrderApi
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class JokerTask(
        private val jokerOrderApi: GameOrderApi,
        private val jokerBetOrderDao: JokerBetOrderDao,
        private val betCacheUtil: BetCacheUtil
) {

    @Scheduled(cron="0/10 * *  * * ? ")   //每10秒执行一
    fun syncOrder() {
        val endDate = LocalDateTime.now()
        val startDate = endDate.minusHours(1)
        val unionId = jokerOrderApi.synOrder(startTime = startDate, endTime = endDate)

        betCacheUtil.handler(unionId)

    }



}
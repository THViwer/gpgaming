package com.onepiece.treasure.task

import com.onepiece.treasure.core.order.JokerBetOrderDao
import com.onepiece.treasure.games.GameOrderApi
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class JokerTask(
        private val jokerOrderApi: GameOrderApi,
        private val jokerBetOrderDao: JokerBetOrderDao,
        private val betCacheUtil: BetCacheUtil
) {
    private val log = LoggerFactory.getLogger(JokerTask::class.java)

    @Scheduled(cron="0/10 * *  * * ? ")   //每10秒执行一
    fun syncOrder() {
        val endTime = LocalDateTime.now().plusHours(1)
        val startTime = endTime.minusHours(2)

        log.info("startTime = $startTime, endTime = $endTime")

        val unionId = jokerOrderApi.synOrder(startTime = startTime, endTime = endTime)

        betCacheUtil.handler(unionId)

    }



}
package com.onepiece.treasure.task

import com.onepiece.treasure.games.GameOrderApi
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class Cta666Task(

        private val cta666GameOrderApi: GameOrderApi,
        private val betCacheUtil: BetCacheUtil
) {
    private val log = LoggerFactory.getLogger(Cta666Task::class.java)

    @Scheduled(cron="0/10 * *  * * ? ")
    fun syncOrder() {
        val endTime = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0)
        val startTime = endTime.minusHours(2)

        log.info("startTime = $startTime, endTime = $endTime")

        val unionId = cta666GameOrderApi.synOrder(startTime = startTime, endTime = endTime)

        betCacheUtil.handler(unionId)

    }



}
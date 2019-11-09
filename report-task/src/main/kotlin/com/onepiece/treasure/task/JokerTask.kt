package com.onepiece.treasure.task

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.games.slot.joker.JokerApi
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class JokerTask(
        private val betCacheUtil: BetCacheUtil,
        private val platformBindService: PlatformBindService,
        private val jokerApi: JokerApi
) {
    private val log = LoggerFactory.getLogger(JokerTask::class.java)

    var running = false

    @Scheduled(cron="0/30 * *  * * ? ")
    fun syncOrder() {
        if (running) return

        running = true

        try {
            val endTime = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0)
            val startTime = endTime.minusHours(2)

            log.info("startTime = $startTime, endTime = $endTime")


            val binds = platformBindService.find(platform = Platform.Joker)
            binds.forEach {
                val unionId = jokerApi.retrieveTransactions(token = it.clientToken as DefaultClientToken, startTime = startTime, endTime = endTime)
                betCacheUtil.handler(unionId)
            }
        } finally {
            running = false
        }
    }

}
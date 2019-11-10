package com.onepiece.treasure.task

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.games.live.ct.CTApi
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CTTask(
        private val platformBindService: PlatformBindService,
        private val cta666Api: CTApi,
        private val betCacheUtil: BetCacheUtil
) {
    private val log = LoggerFactory.getLogger(CTTask::class.java)

    var running = false

    @Scheduled(cron="0/10 * *  * * ? ")
    fun syncOrder() {

        if (running) return
        running = true

        val endTime = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0)
        val startTime = endTime.minusHours(2)

        log.info("startTime = $startTime, endTime = $endTime")

        try {
            val binds = platformBindService.find(platform = Platform.CT)
            binds.forEach {
                val cacheId = cta666Api.getReport(it.clientToken as DefaultClientToken)
                betCacheUtil.handler(cacheId)
            }
        } finally {
            running = false
        }
    }

}
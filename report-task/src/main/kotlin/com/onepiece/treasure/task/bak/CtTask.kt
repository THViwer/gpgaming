package com.onepiece.treasure.task.bak

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.live.ct.CtService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CtTask(
        private val platformBindService: PlatformBindService,
        private val ctService: CtService,
        private val betCacheUtil: BetCacheUtil
) {
    private val log = LoggerFactory.getLogger(CtTask::class.java)

    var running = false

//    @Scheduled(cron="0/10 * *  * * ? ")
    fun syncOrder() {

        if (running) return
        running = true

        val endTime = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0)
        val startTime = endTime.minusHours(2)

        log.info("startTime = $startTime, endTime = $endTime")

        try {
            val binds = platformBindService.find(platform = Platform.CT)
            binds.forEach {
                val syncBetOrderReq = GameValue.SyncBetOrderReq(token = it.clientToken, startTime = startTime, endTime = endTime)
                val cacheId = ctService.asynBetOrder(syncBetOrderReq)
                betCacheUtil.handler(cacheId)
            }
        } finally {
            running = false
        }
    }

}
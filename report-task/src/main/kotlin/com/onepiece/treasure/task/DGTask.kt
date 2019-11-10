package com.onepiece.treasure.task

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.live.ct.CTApi
import com.onepiece.treasure.games.live.dg.DGApi
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DGTask(
        private val platformBindService: PlatformBindService,
        private val dgPlatformApi: PlatformApi,
        private val betCacheUtil: BetCacheUtil
) {
    private val log = LoggerFactory.getLogger(DGTask::class.java)

    var running = false

    @Scheduled(cron="0/10 * *  * * ? ")
    fun syncOrder() {

        if (running) return
        running = true

        val endTime = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0)
        val startTime = endTime.minusHours(2)

        log.info("startTime = $startTime, endTime = $endTime")

        try {
            val binds = platformBindService.find(platform = Platform.DG)
            binds.forEach {
                val syncBetOrderReq = GameValue.SyncBetOrderReq(token = it.clientToken, startTime = startTime, endTime = endTime)
                val cacheId = dgPlatformApi.asynBetOrder(syncBetOrderReq)
                betCacheUtil.handler(cacheId)
            }
        } finally {
            running = false
        }
    }

}
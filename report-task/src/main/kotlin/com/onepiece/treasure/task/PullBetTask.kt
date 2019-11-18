package com.onepiece.treasure.task

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.slot.joker.JokerService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PullBetTask(
        private val platformBindService: PlatformBindService,
        private val jokerService: JokerService
) {

    private val log = LoggerFactory.getLogger(PullBetTask::class.java)

    @Scheduled(cron="0/30 * *  * * ? ")
    fun start() {
        val platformBinds = platformBindService.all()

        platformBinds.forEach {
            when (it.platform) {
                Platform.Joker -> {
                    log.info("厅主Id:${it.clientId}, 平台：${it.platform}, 开始执行拉取订单任务")
                    val endTime = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0)
                    val startTime = endTime.minusHours(2)

                    val pullBetOrderReq = GameValue.PullBetOrderReq(clientId = it.clientId, token = it.clientToken, startTime = startTime, endTime = endTime)
                    jokerService.pullBetOrders(pullBetOrderReq)
                }
                else -> {}

            }
        }
    }


}
package com.onepiece.treasure.task

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.service.BetOrderService
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.live.EvolutionService
import com.onepiece.treasure.games.live.FggService
import com.onepiece.treasure.games.slot.joker.JokerService
import com.onepiece.treasure.games.sport.lbc.LbcService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean

@Component
class PullBetTask(
        private val platformBindService: PlatformBindService,
        private val jokerService: JokerService,
        private val evolutionService: EvolutionService,
        private val lbcService: LbcService,
        private val fggService: FggService,
        private val betOrderService: BetOrderService
) {

    private val log = LoggerFactory.getLogger(PullBetTask::class.java)

    private val running = AtomicBoolean(false)

//    @Scheduled(cron="0 0/1 *  * * ? ")
    fun evolutionTask() {
        val platformBins = platformBindService.find(Platform.Evolution)

        val startTime = LocalDateTime.now().minusMinutes(10)
        val endTime = startTime.plusMinutes(5)
        platformBins.forEach {
            val defaultPullBetOrderReq = GameValue.PullBetOrderReq(clientId = it.clientId, token = it.clientToken, startTime = startTime, endTime = endTime)
            val orders = evolutionService.pullBetOrders(pullBetOrderReq = defaultPullBetOrderReq)
            asyncBatch(orders)
        }
    }


    fun asyncBatch(orders: List<BetOrderValue.BetOrderCo>) {
        if (orders.isEmpty()) return

        betOrderService.batch(orders)
    }


    @Scheduled(cron="0/10 * *  * * ? ")
    fun start() {

        if (!running.getAndSet(true)) return

        val platformBinds = platformBindService.all()


        val defaultEndTime = LocalDateTime.now()
        val defaultStartTime = defaultEndTime.minusMinutes(15)

        platformBinds.forEach {

            val defaultPullBetOrderReq = GameValue.PullBetOrderReq(clientId = it.clientId, token = it.clientToken, startTime = defaultStartTime, endTime = defaultEndTime)

            log.info("厅主Id:${it.clientId}, 平台：${it.platform}, 开始执行拉取订单任务")

            try {
                val orders = when (it.platform) {
//                    Platform.Joker -> {
//                        val pullBetOrderReq = defaultPullBetOrderReq.copy(startTime = defaultPullBetOrderReq.startTime.minusHours(1),
//                                endTime = defaultPullBetOrderReq.endTime.plusDays(1))
//                        jokerService.pullBetOrders(pullBetOrderReq)
//                    }
//                    Platform.Lbc -> lbcService.pullBetOrders(pullBetOrderReq = defaultPullBetOrderReq)


                    // 测试
//                    Platform.Evolution -> evolutionService.pullBetOrders(pullBetOrderReq = defaultPullBetOrderReq)
                    Platform.Fgg -> fggService.pullBetOrders(pullBetOrderReq = defaultPullBetOrderReq)
                    else -> {
                        emptyList()
                    }
                }

                if (orders.isEmpty()) return@forEach

                betOrderService.batch(orders)
            } catch (e: Exception) {
                log.error("", e)
            }
        }

        running.set(false)
    }

}
package com.onepiece.treasure.task

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.service.BetOrderService
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.games.GameApi
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean

@Component
class PullBetTask(
        private val platformBindService: PlatformBindService,
        private val gameApi: GameApi,
        private val betOrderService: BetOrderService
) {

    private val log = LoggerFactory.getLogger(PullBetTask::class.java)
    private val running = AtomicBoolean(false)

    private fun startTask(platform: Platform, startTime: LocalDateTime, endTime: LocalDateTime) {
        val binds = platformBindService.find(platform)
        binds.forEach { bind ->
            log.info("厅主Id:${bind.clientId}, 平台：${bind.platform}, 开始执行拉取订单任务")
            val orders = gameApi.pullBets(platformBind = bind, startTime = startTime, endTime = endTime)
            asyncBatch(orders)
        }
    }

    private fun asyncBatch(orders: List<BetOrderValue.BetOrderCo>) {
        if (orders.isEmpty()) return
        betOrderService.batch(orders)
    }


    // * 调用次数限制: 25次/10分钟 (以每个propertyId计算)
    // @Scheduled(cron="0/5 * *  * * ? ")
    fun allBetTask() {
        val startTime = LocalDateTime.now().minusMinutes(20)
        val endTime = LocalDateTime.now()

        this.startTask(platform = Platform.AllBet, startTime = startTime, endTime = endTime)
    }

    // if apply platform the api allowed frequency is 20 seconds.
    // if not apply platform the api allowed frequency is 60 seconds.
    // @Scheduled(cron="0/30 * *  * * ? ")
    fun sexyGamingTask() {
        val startTime = LocalDateTime.now().minusMinutes(5)
        val endTime = LocalDateTime.now()
        this.startTask(platform = Platform.SexyGaming, startTime = startTime, endTime = endTime)
    }


//    @Scheduled(cron="0 0/3 *  * * ? ")
//    fun evolutionTask() {
//        val startTime = LocalDateTime.now().minusMinutes(5)
//        val endTime = LocalDateTime.now()
//
//        this.startTask(platform = Platform.Evolution, startTime = startTime, endTime = endTime)
//    }
//
     @Scheduled(cron="0 0/1 *  * * ? ")
    fun spadeGamingTask() {
        val startTime = LocalDateTime.now().minusMinutes(5)
        val endTime = LocalDateTime.now()
        this.startTask(platform = Platform.SpadeGaming, startTime = startTime, endTime = endTime)
    }
















//    @Scheduled(cron="0/10 * *  * * ? ")
//    fun jokerTask() {
//        val startTime = LocalDateTime.now().minusHours(1)
//        val endTime = LocalDateTime.now().plusHours(1)
//        this.startTask(platform = Platform.Joker, startTime = startTime, endTime = endTime)
//    }
//
//    // 5分钟一次
//     @Scheduled(cron="0 0/3 *  * * ? ")
//    fun ttgTask() {
//        val startTime = LocalDateTime.now().minusMinutes(5)
//        val endTime = LocalDateTime.now()
//        this.startTask(platform = Platform.TTG, startTime = startTime, endTime = endTime)
//    }
//
//     @Scheduled(cron="0 0/3 *  * * ? ")
//    fun microGamingTask() {
//        val startTime = LocalDateTime.now().minusMinutes(5)
//        val endTime = LocalDateTime.now()
//        this.startTask(platform = Platform.MicroGaming, startTime = startTime, endTime = endTime)
//    }
//
    @Scheduled(cron="0/10 * *  * * ? ")
    fun start() {
        val endTime = LocalDateTime.now()
        val starTime = endTime.minusMinutes(5)

        val platforms = listOf(
//                Platform.Lbc,
//                Platform.Bcs,
//                Platform.GGFishing,
//                Platform.CMD




                Platform.DreamGaming
//                Platform.Fgg,
//                Platform.Pragmatic
        )

        platforms.forEach { platform ->
            try {
                this.startTask(platform = platform, startTime = starTime, endTime = endTime)
            } catch (e: Exception) {
                log.error("", e)
            }
        }
    }

}
package com.onepiece.gpgaming.task

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.PlatformBind
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.service.BetOrderService
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.games.GameApi
import com.onepiece.gpgaming.utils.RedisService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean


@Component
class PullBetTask(
        private val platformBindService: PlatformBindService,
        private val gameApi: GameApi,
        private val betOrderService: BetOrderService,
        private val redisService: RedisService
) {

    private val log = LoggerFactory.getLogger(PullBetTask::class.java)
    private val running = AtomicBoolean(false)

//    private fun startTask(platform: Platform, startTime: LocalDateTime, endTime: LocalDateTime) {
//        val binds = platformBindService.find(platform)
//        binds.forEach { bind ->
//            log.info("厅主Id:${bind.clientId}, 平台：${bind.platform}, 开始执行拉取订单任务")
//            val orders = gameApi.pullBets(platformBind = bind, startTime = startTime, endTime = endTime)
//            asyncBatch(orders)
//        }
//    }

    private fun asyncBatch(orders: List<BetOrderValue.BetOrderCo>) {
        if (orders.isEmpty()) return
        betOrderService.batch(orders)
    }


    @Scheduled(cron="0/20 * *  * * ? ")
    fun execute() {
        if (running.get()) return
        running.set(true)

        //TODO 暂时过滤其它厅主的
//        val binds = platformBindService.all().filter { it.platform == Platform.AsiaGamingSlot || it.platform == Platform.AsiaGamingLive }.filter { it.clientId == 1 } // && it.platform == Platform.MicroGaming
        val binds = platformBindService.all().filter { it.clientId == 1 } // && it.platform == Platform.MicroGaming

        binds.parallelStream().forEach  { bind ->
            this.executePlatform(bind)
        }

        running.set(false)
    }

    private fun executePlatform(bind: PlatformBind) {

        try {
            this.handler(bind = bind) { startTime, endTime ->
                gameApi.pullBets(platformBind = bind, startTime = startTime, endTime = endTime)
            }
        } catch (e: Exception) {
            log.info("厅主：${bind.clientId}, 平台：${bind.platform}, 执行任务失败", e)
        }
    }

    private fun handler(
            bind: PlatformBind,
            pull: (startTime: LocalDateTime, endTime: LocalDateTime) -> List<BetOrderValue.BetOrderCo>
    ) {

        val redisKey = "pull:task:${bind.clientId}:${bind.platform}"

        val startTime = redisService.get(key = redisKey, clz = String::class.java) {
            //TODO 暂时10分钟 线上用30分钟
            "${LocalDateTime.now().minusMinutes(10)}"
        }!!.let { LocalDateTime.parse(it) }

        if (!this.canExecutePlatform(startTime = startTime, platform = bind.platform)) return

        val endTime = startTime.plusMinutes(3)

        log.info("厅主：${bind.clientId}, 平台：${bind.platform}, 开始时间任务：${LocalDateTime.now()}, 查询开始时间：$startTime, 查询结束时间${endTime}")
        val orders = pull(startTime, endTime)
        this.asyncBatch(orders)

        redisService.put(key = redisKey, value = startTime.plusMinutes(2))
    }

    private fun canExecutePlatform(startTime: LocalDateTime, platform: Platform): Boolean {
        return when (platform) {
            Platform.Evolution,
            Platform.Joker,
            Platform.TTG,
            Platform.AllBet,
            Platform.DreamGaming,
            Platform.SpadeGaming,
            Platform.SexyGaming,
            Platform.GoldDeluxe,
            Platform.SaGaming,
            Platform.SimplePlay,
            Platform.GamePlay,
            Platform.MicroGaming,
            Platform.MicroGamingLive,
            Platform.AsiaGamingSlot,
            Platform.AsiaGamingLive -> {
                val duration = Duration.between(startTime, LocalDateTime.now())
                val minutes: Long = duration.toMinutes() //相差的分钟数
                minutes > 2
            }
            // 不需要判断时间
            Platform.Pragmatic,
            Platform.GGFishing,
            Platform.Bcs,
            Platform.CMD,
            Platform.Lbc,
            Platform.Fgg  -> true

            else -> {
                log.warn("该平台不支持同步订单:${platform}")
                false
            }
        }
    }

}
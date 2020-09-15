//package com.onepiece.gpgaming.task
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.onepiece.gpgaming.beans.enums.Platform
//import com.onepiece.gpgaming.beans.enums.Status
//import com.onepiece.gpgaming.beans.model.PlatformBind
//import com.onepiece.gpgaming.beans.value.database.BetOrderValue
//import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
//import com.onepiece.gpgaming.core.service.BetOrderService
//import com.onepiece.gpgaming.core.service.GamePlatformService
//import com.onepiece.gpgaming.core.service.PlatformBindService
//import com.onepiece.gpgaming.core.utils.PolUtil
//import com.onepiece.gpgaming.games.GameApi
//import com.onepiece.gpgaming.utils.RedisService
//import org.slf4j.LoggerFactory
//import org.springframework.scheduling.annotation.Scheduled
//import org.springframework.stereotype.Component
//import java.time.Duration
//import java.time.LocalDateTime
//import java.util.stream.Collectors
//
//
//@Component
//class PullBetTask(
//        private val platformBindService: PlatformBindService,
//        private val gameApi: GameApi,
//        private val betOrderService: BetOrderService,
//        private val redisService: RedisService,
//        private val gamePlatformService: GamePlatformService,
//        private val polUtil: PolUtil,
//        private val objectMapper: ObjectMapper
//) {
//
//    private val log = LoggerFactory.getLogger(PullBetTask::class.java)
//
////    private fun startTask(platform: Platform, startTime: LocalDateTime, endTime: LocalDateTime) {
////        val binds = platformBindService.find(platform)
////        binds.forEach { bind ->
////            log.info("厅主Id:${bind.clientId}, 平台：${bind.platform}, 开始执行拉取订单任务")
////            val orders = gameApi.pullBets(platformBind = bind, startTime = startTime, endTime = endTime)
////            asyncBatch(orders)
////        }
////    }
//
//    private fun asyncBatch(orders: List<BetOrderValue.BetOrderCo>) {
//        if (orders.isEmpty()) return
//        betOrderService.batch(orders)
//    }
//
//    @Scheduled(cron = "0/20 * *  * * ? ")
//    fun start20Second() {
//        val redisKey = "pull:task:running"
//        this.execute(redisKey = redisKey, time = "")
//    }
//
//    @Scheduled(cron = "0 0 0/1  * * ? ")
//    fun startOneHour() {
//        val redisKey = "pull:task:running:hour"
//        this.execute(redisKey = redisKey, time = ":hour")
//    }
//
//    fun execute(redisKey: String, time: String) {
//        val running = redisService.get(redisKey, Boolean::class.java) ?: false
//        if (running) return else redisService.put(redisKey, true, 5 * 60)
//
//        log.info("定时任务执行中，现在时间：${LocalDateTime.now()}")
//        try {
//
//            val gps = gamePlatformService.all().map { it.platform to it }.toMap()
//
//            // TODO 暂时用AllBet
//            val binds = platformBindService.all()
////                    .filter { it.clientId == 1 && it.platform == Platform.AllBet }
//                    .filter { it.clientId != 4 } //TODO 暂时先关闭BWClub88
//                    .filter { it.status != Status.Delete }
//                    .filter { gps[it.platform]?.status == Status.Normal }
//
//            val list = binds.filter { it.platform != Platform.PlaytechLive }.parallelStream().map { bind ->
//                this.executePlatform(bind, time)
//            }.collect(Collectors.toList())
//
//            log.info("执行成功个数：${list.filter { it }.size}个")
//
//        } finally {
//            redisService.put(redisKey, false)
//        }
//    }
//
//    private fun executePlatform(bind: PlatformBind, time: String): Boolean {
//        return try {
//            this.handler(bind = bind, time = time) { startTime, endTime ->
//                val data = gameApi.pullBets(platformBind = bind, startTime = startTime, endTime = endTime)
//
//                val log = PolUtil.PullOrderLog(clientId = bind.clientId, platform = bind.platform, flag = true, executeTime = LocalDateTime.now(),
//                        response = objectMapper.writeValueAsString(data))
//                polUtil.pol(log)
//
//                data
//            }
//            true
//        } catch (e: Exception) {
//            log.info("厅主：${bind.clientId}, 平台：${bind.platform}, 执行任务失败", e)
//
//            val log = PolUtil.PullOrderLog(clientId = bind.clientId, platform = bind.platform, flag = false, executeTime = LocalDateTime.now(),
//                    response = e.message ?: "")
//            polUtil.pol(log)
//
//            false
//        }
//    }
//
//
//    private fun handler(
//            bind: PlatformBind,
//            time: String,
//            pull: (startTime: LocalDateTime, endTime: LocalDateTime) -> List<BetOrderValue.BetOrderCo>
//    ) {
//
//        val redisKey = "pull:task:${bind.clientId}:${bind.platform}${time}"
//
//        val startTime = redisService.get(key = redisKey, clz = String::class.java) {
//            //TODO 暂时10分钟 线上用30分钟
//            if (time == ":hour") {
//                "${LocalDateTime.now().minusMinutes(90)}"
//            } else {
//                "${LocalDateTime.now().minusMinutes(30)}"
//            }
//        }!!.let { LocalDateTime.parse(it) }
//                .let {
//                    // 如果时间大于20小时 则取最新的20小时内的数据
//                    val hour = Duration.between(it, LocalDateTime.now()).toHours()
//                    if (hour > 20) {
//                        LocalDateTime.now().minusHours(20)
//                    } else {
//                        it
//                    }
//                }
//
//        if (!this.canExecutePlatform(startTime = startTime, platform = bind.platform, time = time)) return
//
//        // 如果距离现在超过30分钟 则每次取10分钟数据
//        val duration = Duration.between(startTime, LocalDateTime.now()).toMinutes()
//        val addMinus = if (duration > 30) 10L else 3L
//
//        val pqStartTime = startTime.minusMinutes(10)
//        val pqEndTime = pqStartTime.plusMinutes(addMinus)
//
//        log.info("厅主：${bind.clientId}, 平台：${bind.platform}, 开始时间任务：${LocalDateTime.now()}, 查询开始时间：$pqStartTime, 查询结束时间${pqEndTime}")
//        val orders = pull(pqStartTime, pqEndTime)
//        this.asyncBatch(orders)
//
//        val v = if (bind.platform == Platform.Pragmatic) {
//            LocalDateTime.now().minusMinutes(5)
//        } else {
//            startTime.plusMinutes(addMinus - 1)
//        }
//
//        redisService.put(key = redisKey, value = v)
//    }
//
//    private fun canExecutePlatform(startTime: LocalDateTime, platform: Platform, time: String): Boolean {
//        return when (platform) {
//            Platform.Evolution,
//            Platform.Joker,
//            Platform.TTG,
//            Platform.AllBet,
//            Platform.DreamGaming,
//            Platform.SpadeGaming,
//            Platform.SexyGaming,
//            Platform.GoldDeluxe,
//            Platform.SaGaming,
//            Platform.SimplePlay,
//            Platform.GamePlay,
//            Platform.MicroGaming,
//            Platform.MicroGamingLive,
//            Platform.AsiaGamingSlot,
//            Platform.AsiaGamingLive,
//            Platform.PlaytechSlot,
//            Platform.EBet,
//            Platform.PlaytechLive -> {
//                val duration = Duration.between(startTime, LocalDateTime.now())
//                val minutes: Long = duration.toMinutes() //相差的分钟数
//
//                val v = if (time == ":hour") 60 else 2
//                minutes > v
//            }
//            Platform.Pragmatic -> {
//                val duration = Duration.between(startTime, LocalDateTime.now())
//                val minutes: Long = duration.toMinutes() //相差的分钟数
//                minutes > 5
//            }
//            // 不需要判断时间
////            Platform.Pragmatic,
//            Platform.GGFishing,
//            Platform.Bcs,
//            Platform.CMD,
//            Platform.Lbc,
//            Platform.Fgg -> {
//                time != ":hour"
//            }
//            else -> {
//                log.warn("该平台不支持同步订单:${platform}")
//                false
//            }
//        }
//    }
//
//}

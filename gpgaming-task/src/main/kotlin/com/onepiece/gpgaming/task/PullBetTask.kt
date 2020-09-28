package com.onepiece.gpgaming.task

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.enums.U9RequestStatus
import com.onepiece.gpgaming.beans.model.PlatformBind
import com.onepiece.gpgaming.beans.model.PullOrderTask
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.ActiveConfig
import com.onepiece.gpgaming.core.dao.PullOrderTaskDao
import com.onepiece.gpgaming.core.service.BetOrderService
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.games.GameApi
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKResponse
import com.onepiece.gpgaming.utils.RedisService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime


@Component
class PullBetTask(
        private val platformBindService: PlatformBindService,
        private val redisService: RedisService,
        private val gameApi: GameApi,
        private val orderTaskDao: PullOrderTaskDao,
        private val betOrderService: BetOrderService,
        private val activeConfig: ActiveConfig
) {

    private val log = LoggerFactory.getLogger(PullBetTask::class.java)

    companion object {
        // 默认拉取5分钟的订单
        const val PULL_ORDER_FIVE_MINUTE = 5L

        //        const val PULL_ORDER_FIVE_MINUTE = 1L

    }

    //    @Scheduled(cron = "0 0/1 *  * * ? ")
    @Scheduled(cron = "0 0,5,10,15,20,25,30,35,40,45,50,55 *  * * ? ")
    fun startByMinute() {
        val binds = platformBindService.all()
//                .filter { it.platform == Platform.GamePlay } //TODO 测试
                .filter { it.status == Status.Normal }
                .filter {
                    when (activeConfig.profile) {
                        "dev" -> it.clientId == 1
                        else -> true
                    }
                }
                .filter {
                    // 这些平台不能同步订单
                    when (it.platform) {
                        // 这些平台没有同步功能
                        Platform.Kiss918,
                        Platform.Pussy888,
                        Platform.Mega,
                        Platform.PNG -> false

                        // 这些平台已被删除
                        Platform.GoldDeluxe,
                        Platform.CT,
                        Platform.Fgg,
                        Platform.Joker -> false

                        // 同AG Live所以不需要拉订单
                        Platform.AsiaGamingSlot -> false

                        else -> true
                    }
                }

        binds.parallelStream().forEach { bind ->

            val preExecuteTime = this.getExecuteCacheKey(bind = bind)

            // 判断是否操作10分钟
            val duration = Duration.between(preExecuteTime, LocalDateTime.now())
            val minute = duration.toMinutes()

            // 获得拉取订单的结束时间
            val endTime = when {
                minute <= 15 -> preExecuteTime.plusMinutes(PULL_ORDER_FIVE_MINUTE)
                minute <= 60 -> preExecuteTime.plusMinutes(15L)
                else -> preExecuteTime.plusMinutes(30L)
            }.let { // 判断如果拉取的时候
                if (Duration.between(it, LocalDateTime.now()).toMinutes() > PULL_ORDER_FIVE_MINUTE) it else LocalDateTime.now().minusMinutes(PULL_ORDER_FIVE_MINUTE)
            }

            execute(bind = bind, startTime = preExecuteTime.minusMinutes(1), endTime = endTime, taskType = PullOrderTask.OrderTaskType.MINUTE)

            this.putExecuteCacheKey(bind = bind, endTime = endTime)
        }
    }

    @Scheduled(cron = "0 0/13 *  * * ? ")
    fun startByHour() {
        val binds = platformBindService.all()
                .filter { it.status != Status.Delete }
                .filter {
                    when (activeConfig.profile) {
                        "dev" -> it.clientId == 1
                        else -> true
                    }
                } //TODO 这里主要为了测试
                .filter {
                    // 这些平台不能同步订单
                    when (it.platform) {
                        Platform.Kiss918,
                        Platform.Pussy888,
                        Platform.Mega,
                        Platform.PNG -> false

                        // 这些平台不需要补单
                        Platform.GGFishing,
                        Platform.Bcs,
                        Platform.CMD,
                        Platform.Lbc -> false

                        // 这些平台已被删除
                        Platform.GoldDeluxe,
                        Platform.CT,
                        Platform.Fgg,
                        Platform.Joker -> false

                        // 同AG Live所以不需要拉订单
                        Platform.AsiaGamingSlot -> false

                        else -> true
                    }
                }

        binds.parallelStream().forEach { bind ->
            val startTime = LocalDateTime.now().minusHours(1)
            val endTime = startTime.plusMinutes(13)

            execute(bind = bind, startTime = startTime.minusMinutes(1), endTime = endTime, taskType = PullOrderTask.OrderTaskType.MINUTE_13)
        }

    }

    @Scheduled(cron = "0 0/28 *  * * ? ")
    fun startByHour2() {
        val binds = platformBindService.all()
                .filter { it.status != Status.Delete }
                .filter {
                    when (activeConfig.profile) {
                        "dev" -> it.clientId == 1
                        else -> true
                    }
                } //TODO 这里主要为了测试
                .filter {
                    // 这些平台不能同步订单
                    when (it.platform) {
                        Platform.Kiss918,
                        Platform.Pussy888,
                        Platform.Mega,
                        Platform.PNG -> false

                        // 这些平台不需要补单
                        Platform.GGFishing,
                        Platform.Bcs,
                        Platform.CMD,
                        Platform.Lbc -> false

                        // 这些平台已被删除
                        Platform.GoldDeluxe,
                        Platform.CT,
                        Platform.Fgg,
                        Platform.Joker -> false

                        // 同AG Live所以不需要拉订单
                        Platform.AsiaGamingSlot -> false

                        else -> true
                    }
                }

        binds.parallelStream().forEach { bind ->
            val startTime = LocalDateTime.now().minusHours(5)
            val endTime = startTime.plusMinutes(28)

            execute(bind = bind, startTime = startTime.minusMinutes(1), endTime = endTime, taskType = PullOrderTask.OrderTaskType.MINUTE_13)
        }

    }

    private fun getExecuteCacheKey(bind: PlatformBind): LocalDateTime {
        val redisKey = "order:task:${bind.clientId}:${bind.platform}"

        val now = LocalDateTime.now()

        val time = redisService.get(redisKey, String::class.java) {
            now.minusHours(2).toString()
        } ?: now.minusHours(2).toString()

        return LocalDateTime.parse(time).let {
            val duration = Duration.between(it, now)
            if (duration.toMinutes() <= 5) now.minusMinutes(10) else it
        }
    }

    private fun putExecuteCacheKey(bind: PlatformBind, endTime: LocalDateTime) {
        val redisKey = "order:task:${bind.clientId}:${bind.platform}"
        redisService.put(redisKey, endTime.toString())
    }

    fun execute(bind: PlatformBind, taskType: PullOrderTask.OrderTaskType, startTime: LocalDateTime, endTime: LocalDateTime) {

        var gameResponse: GameResponse<List<BetOrderValue.BetOrderCo>> = GameResponse.of(emptyList())
        try {
            //            val cut = Duration.between(startTime, endTime)
//            val cutMinute = cut.toMinutes()
//            val curTime = cutMinute / 10
//            val add = if (cutMinute % 10 == 0L) 0 else 1

            when (bind.platform) {
                Platform.AsiaGamingSlot,
                Platform.AsiaGamingLive -> {
                    var flag = true
                    var cutTime = startTime
                    while (flag) {
                        val cutStartTime = cutTime

                        cutTime = cutTime.plusMinutes(10)
                        flag = cutTime.plusSeconds(1).isBefore(endTime)

                        val catEndTime = if (flag) endTime else cutTime

                        gameResponse = gameApi.pullBets(platformBind = bind, startTime = cutStartTime, endTime = catEndTime)
                        this.saveOrderTask(bind = bind, startTime = cutStartTime, endTime = catEndTime, okResponse = gameResponse.okResponse, taskType = taskType)

                        val orders = gameResponse.data ?: emptyList()
                        if (orders.isNotEmpty()) {
                            betOrderService.batch(orders = orders)
                        }
                    }
                }
                else -> {
                    gameResponse = gameApi.pullBets(platformBind = bind, startTime = startTime, endTime = endTime)
                    this.saveOrderTask(bind = bind, startTime = startTime, endTime = endTime, okResponse = gameResponse.okResponse, taskType = taskType)

                    val orders = gameResponse.data ?: emptyList()
                    if (orders.isNotEmpty()) {
                        betOrderService.batch(orders = orders)
                    }
                }
            }


        } catch (e: Exception) {
            log.info("厅主：${bind.clientId}, 平台：${bind.platform}, 执行任务失败", e)

            val message = e.message ?: e.localizedMessage
            val okResponse = gameResponse.okResponse.copy(message = message, status = U9RequestStatus.Fail)
            this.saveOrderTask(bind = bind, startTime = startTime, endTime = endTime, okResponse = okResponse, taskType = taskType)
        }

    }

    private fun saveOrderTask(bind: PlatformBind, taskType: PullOrderTask.OrderTaskType, startTime: LocalDateTime, endTime: LocalDateTime, okResponse: OKResponse) {

        // TODO 请求平台 失败的时候才记录
        if (!okResponse.ok) {
            val formParam = okResponse.okParam.formParam.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
            val headers = okResponse.okParam.headers.map { "${it.key}=${it.value}" }.joinToString(separator = "&")

            val task = PullOrderTask(id = -1, clientId = bind.clientId, platform = bind.platform, param = okResponse.param,
                    path = okResponse.url, response = okResponse.response, type = taskType, status = okResponse.status,
                    startTime = startTime, endTime = endTime, message = okResponse.message ?: "", formParam = formParam,
                    headers = headers, nonce = okResponse.okParam.nonce)
            orderTaskDao.create(task)
        }

    }

}
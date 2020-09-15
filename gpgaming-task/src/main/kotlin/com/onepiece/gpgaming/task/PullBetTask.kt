package com.onepiece.gpgaming.task

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.PlatformBind
import com.onepiece.gpgaming.beans.model.PullOrderTask
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
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
        private val betOrderService: BetOrderService
) {

    companion object {
        // 默认拉取5分钟的订单
        const val PULL_ORDER_FIVE_MINUTE = 5L

        //        const val PULL_ORDER_FIVE_MINUTE = 1L
        val log = LoggerFactory.getLogger(PullBetTask::class.java)

    }

    @Scheduled(cron = "* 0/5 *  * * ? ")
//    @Scheduled(cron = "0/10 * *  * * ? ")
    fun startByMinute() {
        val binds = platformBindService.all()
//                .filter { it.clientId == 1 } //TODO 这里主要为了测试
//                .filter {
//                    when (it.platform) {
////                        Platform.GamePlay
////                        Platform.AsiaGamingSlot
////                        Platform.AsiaGamingLive
//                        -> true
//                        else -> false
//                    }
//                }

                .filter { it.status != Status.Delete }
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

                        else -> true
                    }
                }

        binds.forEach { bind ->

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

            execute(bind = bind, startTime = preExecuteTime, endTime = endTime, taskType = PullOrderTask.OrderTaskType.MINUTE)

            this.putExecuteCacheKey(bind = bind, endTime = endTime)
        }
    }

    @Scheduled(cron = "* 0/13 *  * * ? ")
    fun startByHour() {
        val binds = platformBindService.all()
                .filter { it.status != Status.Delete }
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
                        Platform.Lbc,
                        Platform.Fgg -> false

                        else -> true
                    }
                }

        binds.forEach { bind ->
            val startTime = LocalDateTime.now().minusHours(1)
            val endTime = startTime.plusMinutes(13)

            execute(bind = bind, startTime = startTime, endTime = endTime, taskType = PullOrderTask.OrderTaskType.MINUTE_13)
        }

    }

    @Scheduled(cron = "* 0/28 *  * * ? ")
    fun startByHour2() {
        val binds = platformBindService.all()
                .filter { it.status != Status.Delete }
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
                        Platform.Lbc,
                        Platform.Fgg -> false

                        else -> true
                    }
                }

        binds.forEach { bind ->
            val startTime = LocalDateTime.now().minusHours(5)
            val endTime = startTime.plusMinutes(28)

            execute(bind = bind, startTime = startTime, endTime = endTime, taskType = PullOrderTask.OrderTaskType.MINUTE_13)
        }

    }

    private fun getExecuteCacheKey(bind: PlatformBind): LocalDateTime {
        val redisKey = "order:task:${bind.clientId}:${bind.platform}"

        val time = redisService.get(redisKey, String::class.java) {
            LocalDateTime.now().minusHours(2).toString()
        } ?: LocalDateTime.now().minusHours(2).toString()

        return LocalDateTime.parse(time)
    }

    private fun putExecuteCacheKey(bind: PlatformBind, endTime: LocalDateTime) {
        val redisKey = "order:task:${bind.clientId}:${bind.platform}"
        redisService.put(redisKey, endTime.toString())
    }

    fun execute(bind: PlatformBind, taskType: PullOrderTask.OrderTaskType, startTime: LocalDateTime, endTime: LocalDateTime) {

        var gameResponse : GameResponse<List<BetOrderValue.BetOrderCo>> = GameResponse.of(emptyList())
        try {
            gameResponse = gameApi.pullBets(platformBind = bind, startTime = startTime, endTime = endTime)
            this.saveOrderTask(bind = bind, startTime = startTime, endTime = endTime, okResponse = gameResponse.okResponse, taskType = taskType)

            val orders = gameResponse.data ?: emptyList()
            if (orders.isNotEmpty()) {
                betOrderService.batch(orders = orders)
            }
        } catch (e: Exception) {
            log.info("厅主：${bind.clientId}, 平台：${bind.platform}, 执行任务失败", e)

            val okResponse = gameResponse.okResponse.copy(message = e.message?: "")
            this.saveOrderTask(bind = bind, startTime = startTime, endTime = endTime, okResponse = gameResponse.okResponse, taskType = taskType)
        }

    }

    private fun saveOrderTask(bind: PlatformBind, taskType: PullOrderTask.OrderTaskType, startTime: LocalDateTime, endTime: LocalDateTime, okResponse: OKResponse) {
        val task = PullOrderTask(id = -1, clientId = bind.clientId, platform = bind.platform, param = okResponse.param,
                path = okResponse.url, response = okResponse.response, type = taskType, ok = okResponse.ok,
                startTime = startTime, endTime = endTime, message = okResponse.message ?: "")
        orderTaskDao.create(task)

    }

}
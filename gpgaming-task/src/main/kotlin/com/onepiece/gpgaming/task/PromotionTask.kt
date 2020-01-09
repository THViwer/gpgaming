package com.onepiece.gpgaming.task

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.PromotionDailyReport
import com.onepiece.gpgaming.beans.model.PromotionPlatformDailyReport
import com.onepiece.gpgaming.beans.model.TaskTimerType
import com.onepiece.gpgaming.core.service.PromotionDailyReportService
import com.onepiece.gpgaming.core.service.PromotionPlatformDailyReportService
import com.onepiece.gpgaming.core.service.TaskTimerService
import com.onepiece.gpgaming.core.service.TransferOrderService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class PromotionTask(
        private val transferOrderService: TransferOrderService,
        private val promotionPlatformDailyReportService: PromotionPlatformDailyReportService,
        private val promotionDailyReportService: PromotionDailyReportService,
        private val taskTimerService: TaskTimerService
) {

    private val log = LoggerFactory.getLogger(PromotionTask::class.java)

    private fun tryLock(localDate: LocalDate, type: TaskTimerType, function: () -> Unit) {

        val state = taskTimerService.lock(day = localDate, type = type)
        if (!state) return

        try {
            function()
            taskTimerService.done(day = localDate, type = type)
        } catch (e: Exception) {
            log.error("执行报表任务失败:", e)
            taskTimerService.fail(day = localDate, type = type)
        }
    }

    @Scheduled(cron = "0 0 3 * * ?")
    fun execute() {

        val startDate = LocalDate.now().minusDays(1)
        tryLock(localDate = startDate, type = TaskTimerType.PromotionPlatformDaily) {
            val transferReports = transferOrderService.report(startDate = startDate)

            if (transferReports.isNotEmpty()) {

                val now = LocalDateTime.now()
                val reports = transferReports.map {

                    PromotionPlatformDailyReport(id = -1, clientId = it.clientId, platform = it.platform, day = startDate, promotionId = it.promotionId, promotionAmount = it.promotionAmount,
                            createdTime = now, status = Status.Normal)
                }

                promotionPlatformDailyReportService.create(reports)
            }

        }


        tryLock(localDate = startDate, type = TaskTimerType.PromotionDaily) {
            val transferReports = promotionPlatformDailyReportService.statistical(startDate = startDate)

            if (transferReports.isNotEmpty()) {

                val now = LocalDateTime.now()
                val reports = transferReports.map {

                    PromotionDailyReport(id = -1, clientId = it.clientId, day = startDate, promotionId = it.promotionId, promotionAmount = it.promotionAmount,
                            createdTime = now, status = Status.Normal)
                }

                promotionDailyReportService.create(reports)
            }

        }



    }


}
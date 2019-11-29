package com.onepiece.treasure.task

import com.onepiece.treasure.beans.model.TaskTimerType
import com.onepiece.treasure.core.service.*
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ReportTask(
        private val memberPlatformDailyReportService: MemberPlatformDailyReportService,
        private val memberDailyReportService: MemberDailyReportService,
        private val clientPlatformDailyReportService: ClientPlatformDailyReportService,
        private val clientDailyReportService: ClientDailyReportService,
        private val reportService: ReportService,

        private val taskTimerService: TaskTimerService
) {

    private val log = LoggerFactory.getLogger(ReportTask::class.java)

    @Scheduled(cron = "0 0 2 * * ?")
    fun start() {
        val localDate = LocalDate.now().minusDays(1)

        this.startMemberPlatformDailyReport(localDate)

        this.startMemberReport(localDate)

        this.startClientPlatformReport(localDate)

        this.startClientReport(localDate)

    }

    fun tryLock(localDate: LocalDate, type: TaskTimerType, function: () -> Unit) {

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

    // 会员平台日报表
    fun startMemberPlatformDailyReport(startDate: LocalDate) {
        tryLock(localDate = startDate, type = TaskTimerType.MemberPlatformDaily) {
            val data = reportService.startMemberPlatformDailyReport(memberId = null, startDate = startDate)
            memberPlatformDailyReportService.create(data)
        }
    }

    // 会员日报表
    fun startMemberReport(startDate: LocalDate) {
        tryLock(localDate = startDate, type = TaskTimerType.MemberDaily) {
            val data = reportService.startMemberReport(memberId = null, startDate = startDate)
            memberDailyReportService.create(data)
        }
    }

    // 厅主平台日报表
    fun startClientPlatformReport(startDate: LocalDate) {
        tryLock(localDate = startDate, type = TaskTimerType.ClientPlatformDaily) {
            val data = reportService.startClientPlatformReport(clientId = null, startDate= startDate)
            clientPlatformDailyReportService.create(data)
        }

    }

    // 厅主报表
    fun startClientReport(startDate: LocalDate) {
        tryLock(localDate = startDate, type = TaskTimerType.ClientDaily) {
            val data = reportService.startClientReport(clientId = null, startDate = startDate)
            clientDailyReportService.create(data)
        }
    }

}
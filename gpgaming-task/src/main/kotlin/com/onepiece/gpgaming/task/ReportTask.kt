package com.onepiece.gpgaming.task

import com.onepiece.gpgaming.beans.model.TaskTimerType
import com.onepiece.gpgaming.core.dao.SaleDailyReportDao
import com.onepiece.gpgaming.core.dao.SaleMonthReportDao
import com.onepiece.gpgaming.core.service.AgentDailyReportService
import com.onepiece.gpgaming.core.service.AgentMonthReportService
import com.onepiece.gpgaming.core.service.ClientDailyReportService
import com.onepiece.gpgaming.core.service.ClientPlatformDailyReportService
import com.onepiece.gpgaming.core.service.MemberDailyReportService
import com.onepiece.gpgaming.core.service.ReportService
import com.onepiece.gpgaming.core.service.TaskTimerService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ReportTask(
        private val memberDailyReportService: MemberDailyReportService,
        private val agentDailyReportService: AgentDailyReportService,
        private val agentMonthReportService: AgentMonthReportService,
        private val clientDailyReportService: ClientDailyReportService,
        private val saleDailyReportDao: SaleDailyReportDao,
        private val saleMonthReportDao: SaleMonthReportDao,
        private val reportService: ReportService,
        private val clientPlatformDailyReportService: ClientPlatformDailyReportService,

        private val taskTimerService: TaskTimerService
) {

    private val log = LoggerFactory.getLogger(ReportTask::class.java)

    @Scheduled(cron = "0 0 2 * * ?")
    fun start() {
        val localDate = LocalDate.now().minusDays(1)

//        this.startMemberPlatformDailyReport(localDate)

        this.startMemberReport(startDate = localDate)

        this.startAgentReport(startDate = localDate)

        this.startAgentMonthReport(startDate = localDate)

        this.startSaleReport(startDate = localDate)

        this.startSaleMonthReport(startDate = localDate)

        this.startClientPlatformReport(localDate)

        this.startClientReport(startDate = localDate)
    }

    @Scheduled(cron = "0 0 3 1 * ?")
    fun startMonth() {
        val localDate = LocalDate.now().minusMonths(1)
        this.startAgentMonthReport(startDate = localDate)
    }


    private fun tryLock(localDate: LocalDate, type: TaskTimerType, function: () -> Unit) {

        function()
//        val state = taskTimerService.lock(day = localDate, type = type)
//        if (!state) return
//
//        try {
//            function()
//            taskTimerService.done(day = localDate, type = type)
//        } catch (e: Exception) {
//            log.error("执行报表任务失败:", e)
//            taskTimerService.fail(day = localDate, type = type)
//        }
    }

    // 会员平台日报表
//    fun startMemberPlatformDailyReport(startDate: LocalDate) {
//        tryLock(localDate = startDate, type = TaskTimerType.MemberPlatformDaily) {
//            val data = reportService.startMemberPlatformDailyReport(startDate = startDate)
//            memberPlatformDailyReportService.create(data)
//        }
//    }

    // 会员日报表
    fun startMemberReport(startDate: LocalDate) {
        tryLock(localDate = startDate, type = TaskTimerType.MemberDaily) {
            val data = reportService.startMemberReport(startDate = startDate)
            memberDailyReportService.create(data)
        }
    }

    // 电销日报表
    fun startSaleReport(startDate: LocalDate) {
        tryLock(localDate = startDate, type = TaskTimerType.SaleDaily) {
            val data = reportService.startSaleReport(startDate)
            saleDailyReportDao.batch(data = data)
        }
    }

    // 电销月报表
    fun startSaleMonthReport(startDate: LocalDate) {
        if (startDate.dayOfMonth != 1) return

        tryLock(localDate = startDate, type = TaskTimerType.SaleMonth) {
            val data = reportService.startSaleMonthReport(startDate)
            saleMonthReportDao.batch(data = data)
        }
    }

    // 代理日报表
    fun startAgentReport(startDate: LocalDate) {
        tryLock(localDate = startDate, type = TaskTimerType.AgentDaily) {
            val data = reportService.startAgentReport(startDate =  startDate)
            agentDailyReportService.create(data = data)
        }
    }

    // 代理月报表
    fun startAgentMonthReport(startDate: LocalDate) {

        if (startDate.dayOfMonth != 1) return

        val month = startDate.minusMonths(1)

        tryLock(localDate = month, type = TaskTimerType.AgentMonth) {
            val data = reportService.startAgentMonthReport(today =  month)
            agentMonthReportService.create(data = data)
        }
    }

//    // 厅主平台日报表
    fun startClientPlatformReport(startDate: LocalDate) {
        tryLock(localDate = startDate, type = TaskTimerType.ClientPlatformDaily) {
            val data = reportService.startClientPlatformReport(startDate= startDate)
            clientPlatformDailyReportService.create(data)
        }
    }

    // 厅主报表
    fun startClientReport(startDate: LocalDate) {
        tryLock(localDate = startDate, type = TaskTimerType.ClientDaily) {
            val data = reportService.startClientReport(startDate = startDate)
            clientDailyReportService.create(data)
        }
    }




}

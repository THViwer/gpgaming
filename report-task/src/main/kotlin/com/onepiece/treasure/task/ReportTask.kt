package com.onepiece.treasure.task

import com.onepiece.treasure.core.service.*
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ReportTask(
        private val memberPlatformDailyReportService: MemberPlatformDailyReportService,
        private val memberDailyReportService: MemberDailyReportService,
        private val clientPlatformDailyReportService: ClientPlatformDailyReportService,
        private val clientDailyReportService: ClientDailyReportService,

        private val reportService: ReportService
) {

    // 会员平台日报表
    fun startMemberPlatformDailyReport(startDate: LocalDate) {
        val data = reportService.startMemberPlatformDailyReport(memberId = null, startDate = startDate)
        memberPlatformDailyReportService.create(data)
    }

    // 会员日报表
    fun startMemberReport(startDate: LocalDate) {
        val data = reportService.startMemberReport(memberId = null, startDate = startDate)
        memberDailyReportService.create(data)
    }

    // 厅主平台日报表
    fun startClientPlatformReport(startDate: LocalDate) {
        val data = reportService.startClientPlatformReport(clientId = null, startDate= startDate)
        clientPlatformDailyReportService.create(data)

    }

    // 厅主报表
    fun startClientReport(startDate: LocalDate) {
        val data = reportService.startClientReport(clientId = null, startDate = startDate)
        clientDailyReportService.create(data)
    }

}
package com.onepiece.treasure.task.controller

import com.onepiece.treasure.core.service.ReportService
import com.onepiece.treasure.task.ReportTask
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class DemoController(
        private val reportService: ReportService,
        private val reportTask: ReportTask
) {

    @GetMapping("/report")
    fun start(@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate): Any {

//        reportTask.startMemberPlatformDailyReport(startDate)
//        reportTask.startMemberReport(startDate)
//        reportTask.startClientPlatformReport(startDate)
//        reportTask.startClientReport(startDate)

        return reportService.startClientPlatformReport(clientId = null, startDate= startDate)

    }

    @GetMapping("/kiss918")
    fun kiss918() {

//        val startTime = LocalDate.now().atStartOfDay()
//        val endTime = startTime.plusDays(1)
//        val query = BetOrderValue.Query(startTime = startTime, endTime = endTime, username = "01630399928")
//        kiss918GameOrderApi.query(query)
//
//
//        val startDate = LocalDate.now()
//        val endDate = startDate.plusDays(1)
//        val a = kiss918GameReportApi.memberReport(username = "01630399928", startDate = startDate, endDate = endDate)
//
//        val b = kiss918GameReportApi.clientReport(startDate, endDate)
//

    }

}
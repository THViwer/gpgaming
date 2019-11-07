package com.onepiece.treasure.task.controller

import com.onepiece.treasure.games.old.GameOrderApi
import com.onepiece.treasure.games.old.GameReportApi
import com.onepiece.treasure.task.ReportTask
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class DemoController(
        private val reportTask: ReportTask,
        private val kiss918GameOrderApi: GameOrderApi,
        private val kiss918GameReportApi: GameReportApi
) {

    @GetMapping("/report")
    fun start() {

        val startDate = LocalDate.now()

        reportTask.startMemberPlatformDailyReport(startDate)
        reportTask.startMemberReport(startDate)
        reportTask.startClientPlatformReport(startDate)
        reportTask.startClientReport(startDate)

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
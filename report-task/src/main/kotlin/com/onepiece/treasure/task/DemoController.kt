package com.onepiece.treasure.task

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class DemoController(
        private val reportTask: ReportTask
) {

    @GetMapping("/report")
    fun start() {

        val startDate = LocalDate.now()

        reportTask.startMemberPlatformDailyReport(startDate)
        reportTask.startMemberReport(startDate)
        reportTask.startClientPlatformReport(startDate)
        reportTask.startClientReport(startDate)

    }

}
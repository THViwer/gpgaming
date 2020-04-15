package com.onepiece.gpgaming.task.controller

import com.onepiece.gpgaming.core.service.ReportService
import com.onepiece.gpgaming.task.BackwaterTask
import com.onepiece.gpgaming.task.PromotionTask
import com.onepiece.gpgaming.task.ReportTask
import com.onepiece.gpgaming.task.SexyGamingTask
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class DemoController(
        private val reportService: ReportService,
        private val reportTask: ReportTask,
        private val promotionTask: PromotionTask,
        private val sexyGamingTask: SexyGamingTask,
        private val backwaterTask: BackwaterTask
) {

    @GetMapping("/sexyGaming")
    fun sexyGaming() {
        sexyGamingTask.reconciliation()
    }

    @GetMapping("/report")
    fun start(
            @RequestParam("type") type: String,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate): Any {

        return when (type) {
            "1" -> reportService.startMemberPlatformDailyReport(memberId = null, startDate = startDate)
            "2" -> reportService.startMemberReport(memberId = null, startDate = startDate)
//            "2" -> reportTask.startMemberReport(startDate = startDate)
            "3" -> reportService.startClientPlatformReport(clientId = null, startDate = startDate)
            "4" -> reportService.startClientReport(clientId = null, startDate = startDate)
            "5" -> backwaterTask.start()
            else -> error("参数error")
        }
    }

    @GetMapping("/promotion")
    fun promotion(): Any {
        return promotionTask.execute()
    }

    @GetMapping("/reportTask")
    fun report(): String {
        reportTask.start()
        return "success"
    }

    @GetMapping("/backwater")
    fun backwaterTask(): String {
        backwaterTask.start()
        return "success"
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
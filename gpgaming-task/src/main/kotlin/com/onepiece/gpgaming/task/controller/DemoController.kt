package com.onepiece.gpgaming.task.controller

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.core.service.ReportService
import com.onepiece.gpgaming.games.GameApi
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.task.PromotionTask
import com.onepiece.gpgaming.task.RebateTask
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
        private val rebateTask: RebateTask,
        private val gameApi: GameApi
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
            "member" -> reportService.startMemberReport(startDate = startDate)
            "agent" ->  reportService.startAgentReport(startDate = startDate)
            "agentMonth" ->  reportService.startAgentMonthReport(today = startDate)
            "client" -> reportService.startClientReport(startDate = startDate)
            "clientPlatform" -> reportService.startClientPlatformReport(startDate)
            "sale" -> reportService.startSaleReport(startDate = startDate)
            "saleMonth" -> reportService.startSaleMonthReport(startDate = startDate)
            "market" -> reportService.startMarkReport(startDate = startDate)
            else -> error("参数error")
        }
    }


    @GetMapping("/report2")
    fun start2(
            @RequestParam("type") type: String,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate
    ): Any {

        when (type) {
            "all" -> {
                reportTask.startMemberReport(startDate)
                reportTask.startAgentReport(startDate)
                reportTask.startAgentMonthReport(startDate)
                reportTask.startClientReport(startDate)
                reportTask.startSaleReport(startDate)
                reportTask.startSaleMonthReport(startDate)
            }
            "member" -> reportTask.startMemberReport(startDate)
            "agent" -> reportTask.startAgentReport(startDate)
            "agentMonth" -> reportTask.startAgentMonthReport(startDate)
            "client" -> reportTask.startClientReport(startDate)
            "clientPlatform" -> reportTask.startClientPlatformReport(startDate)
            "rebate" -> rebateTask.start()
            "commission" -> rebateTask.startAgentCommission()
            "sale" -> reportTask.startSaleReport(startDate)
            "saleMonth" -> reportTask.startSaleMonthReport(startDate)
        }

        return "success"
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

    @GetMapping("/clientReport")
    fun reportClient(@RequestParam("startDate") startDate: String): String {
        reportTask.startClientReport(startDate = LocalDate.parse(startDate))
        return "success"
    }

    @GetMapping("/rebate")
    fun backwaterTask(): String {
        rebateTask.start()
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

    @GetMapping("/otherReport")
    fun otherReport(@RequestParam("platform") platform: Platform, @RequestParam("startDate") startDate: String): List<GameValue.PlatformReportData>  {
        return gameApi.queryReport(clientId = 1, platform = platform, startDate = LocalDate.parse(startDate))
    }

}
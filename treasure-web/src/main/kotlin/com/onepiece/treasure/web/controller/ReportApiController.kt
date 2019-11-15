package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.ClientDailyReport
import com.onepiece.treasure.beans.model.ClientPlatformDailyReport
import com.onepiece.treasure.beans.value.database.ClientReportQuery
import com.onepiece.treasure.beans.value.database.MemberReportQuery
import com.onepiece.treasure.beans.value.internet.web.MemberPlatformReportWebVo
import com.onepiece.treasure.beans.value.internet.web.MemberReportWebVo
import com.onepiece.treasure.core.service.*
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RestController
@RequestMapping("/report")
class ReportApiController(
        private val memberPlatformDailyReportService: MemberPlatformDailyReportService,
        private val memberDailyReportService: MemberDailyReportService,
        private val clientPlatformDailyReportService: ClientPlatformDailyReportService,
        private val clientDailyReportService: ClientDailyReportService,
        private val memberService: MemberService,
        private val reportService: ReportService
) : BasicController(), ReportApi {

    private fun <T> includeToday(endDate: LocalDate, function: () -> List<T>): List<T> {
        val isToday = LocalDate.now().until(endDate, ChronoUnit.DAYS) >= 0
        return if (isToday) {
            function()
        } else emptyList()
    }


    @GetMapping("/member/platform")
    override fun memberPlatformDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam(value = "memberId") memberId: Int
    ): List<MemberPlatformReportWebVo> {

//        val memberId = memberService.findByUsername(username)?.id?: return emptyList()

        val query = MemberReportQuery(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate)
        val history = memberPlatformDailyReportService.query(query)

        //查询今天的
        val todayData = this.includeToday(endDate) {
            reportService.startMemberPlatformDailyReport(memberId = memberId, startDate = LocalDate.now())
        }

        val data = history.plus(todayData)
        if (data.isEmpty()) return emptyList()

        val ids = data.map { it.memberId }.toList()
        val members = memberService.findByIds(ids).map { it.id to it }.toMap()

        return data.map {
            val member = members[it.memberId] ?: error(OnePieceExceptionCode.DATA_FAIL)
            with(it) {
                MemberPlatformReportWebVo(day = day, clientId = clientId, memberId = member.id, username = member.username, platform = platform,
                        transferIn = transferIn, transferOut = transferOut)
            }
        }.sortedByDescending { it.day }
    }

    @GetMapping("/member")
    override fun memberDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam(value = "username", required = false) username: String?
    ): List<MemberReportWebVo> {

        val memberId = memberService.findByUsername(username)?.id

        val query = MemberReportQuery(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate)
        val history = memberDailyReportService.query(query)

        //查询今天的
        val todayData = this.includeToday(endDate) {
            reportService.startMemberReport(memberId = memberId, startDate = LocalDate.now())
        }

        val data = history.plus(todayData)
        if (data.isEmpty()) return emptyList()

        val ids = data.map { it.memberId }.toList()
        val members = memberService.findByIds(ids).map { it.id to it }.toMap()

        return data.map {
            val member = members[it.memberId] ?: error(OnePieceExceptionCode.DATA_FAIL)
            with(it) {
                MemberReportWebVo(day = day, clientId = clientId, memberId = member.id, username = member.username,
                        transferIn = transferIn, transferOut = transferOut, depositMoney = depositMoney,
                        withdrawMoney = withdrawMoney)
            }
        }.sortedByDescending { it.day }

    }

    @GetMapping("/client/platform")
    override fun clientPlatformDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): List<ClientPlatformDailyReport> {
        val query = ClientReportQuery(clientId = clientId, startDate = startDate, endDate = endDate)

        //查询今天的
        val todayData = this.includeToday(endDate) {
            reportService.startClientPlatformReport(clientId = clientId, startDate = LocalDate.now())
        }

        return clientPlatformDailyReportService.query(query).plus(todayData).sortedByDescending { it.day }
    }

    @GetMapping("/client")
    override fun clientDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): List<ClientDailyReport> {
        val query = ClientReportQuery(clientId = clientId, startDate = startDate, endDate = endDate)

        //查询今天的
        val todayData = this.includeToday(endDate) {
            reportService.startClientReport(clientId = clientId, startDate = LocalDate.now())
        }

        return clientDailyReportService.query(query).plus(todayData).sortedByDescending { it.day }
    }

}
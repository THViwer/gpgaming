package com.onepiece.treasure.web.controller

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

@RestController
@RequestMapping("/report")
class ReportApiController(
        private val memberPlatformDailyReportService: MemberPlatformDailyReportService,
        private val memberDailyReportService: MemberDailyReportService,
        private val clientPlatformDailyReportService: ClientPlatformDailyReportService,
        private val clientDailyReportService: ClientDailyReportService,
        private val memberService: MemberService
) : BasicController(), ReportApi {


    @GetMapping("/member/platform")
    override fun memberPlatformDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") endDate: LocalDate,
            @RequestParam(value = "username", required = false) username: String?
    ): List<MemberPlatformReportWebVo> {

        val memberId = memberService.findByUsername(username)?.id

        val query = MemberReportQuery(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate)
        val data = memberPlatformDailyReportService.query(query)
        //TODO 查询今天的

        val ids = data.map { it.memberId }.toList()
        val members = memberService.findByIds(ids).map { it.id to it }.toMap()

        return data.map {
            val member = members[it.id]!!
            with(it) {
                MemberPlatformReportWebVo(day = day, clientId = clientId, memberId = member.id, username = member.username, platform = platform,
                        transferIn = transferIn, transferOut = transferOut, bet = bet, win = win)
            }
        }
    }

    @GetMapping("/member")
    override fun memberDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") endDate: LocalDate,
            @RequestParam(value = "username", required = false) username: String?
    ): List<MemberReportWebVo> {

        val memberId = memberService.findByUsername(username)?.id

        val query = MemberReportQuery(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate)
        val data = memberDailyReportService.query(query)
        //TODO 查询今天的

        val ids = data.map { it.memberId }.toList()
        val members = memberService.findByIds(ids).map { it.id to it }.toMap()

        return data.map {
            val member = members[it.id]!!
            with(it) {
                MemberReportWebVo(day = day, clientId = clientId, memberId = member.id, username = member.username,
                        transferIn = transferIn, transferOut = transferOut, bet = bet, win = win, depositMoney = depositMoney,
                        withdrawMoney = withdrawMoney)
            }
        }

    }

    @GetMapping("/client/platform")
    override fun clientPlatformDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") endDate: LocalDate
    ): List<ClientPlatformDailyReport> {
        val query = ClientReportQuery(clientId = clientId, startDate = startDate, endDate = endDate)
        //TODO 查询今天的
        return clientPlatformDailyReportService.query(query)
    }

    @GetMapping("/client")
    override fun clientDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") endDate: LocalDate
    ): List<ClientDailyReport> {
        val query = ClientReportQuery(clientId = clientId, startDate = startDate, endDate = endDate)
        //TODO 查询今天的
        return clientDailyReportService.query(query)
    }

}
package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.SaleDailyReport
import com.onepiece.gpgaming.beans.model.SaleLog
import com.onepiece.gpgaming.beans.model.SaleMonthReport
import com.onepiece.gpgaming.beans.value.database.MemberInfoValue
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
import com.onepiece.gpgaming.beans.value.database.SaleDailyReportValue
import com.onepiece.gpgaming.beans.value.database.SaleLogValue
import com.onepiece.gpgaming.beans.value.database.SaleMonthReportValue
import com.onepiece.gpgaming.beans.value.internet.web.SalesmanValue
import com.onepiece.gpgaming.core.dao.MemberDailyReportDao
import com.onepiece.gpgaming.core.dao.SaleDailyReportDao
import com.onepiece.gpgaming.core.dao.SaleMonthReportDao
import com.onepiece.gpgaming.core.service.MemberInfoService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.SaleLogService
import com.onepiece.gpgaming.core.service.WaiterService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDate

@RestController
@RequestMapping("/sale")
class SalesmanApiController(
        private val saleMonthReportDao: SaleMonthReportDao,
        private val saleDailyReportDao: SaleDailyReportDao,
        private val memberService: MemberService,
        private val memberInfoService: MemberInfoService,
        private val saleLogService: SaleLogService,
        private val waiterService: WaiterService,
        private val memberDailyReportDao: MemberDailyReportDao
): BasicController(), SalesmanApi {

    @GetMapping("/info")
    override fun info(): SalesmanValue.SaleInfo {

        val current = this.current()

        val webSite = webSiteService.getDataByBossId(bossId = current.bossId)
                .first { it.status == Status.Normal && it.clientId == current.clientId }

        val saleCode = when {
            current.id < 10 -> "0000${current.id}"
            current.id < 100 -> "000${current.id}"
            current.id < 1000 -> "00${current.id}"
            current.id < 10000 -> "0${current.id}"
            else -> "${current.id}"
        }
        val saleLink = "https://www.${webSite.domain}/saleCode=${saleCode}"

        return SalesmanValue.SaleInfo(name = current.username, saleCode = saleCode, saleLink = saleLink)
    }

    @GetMapping("/members")
    override fun myMemberList(
            @RequestParam("username", required = false) username: String?,

            @RequestParam("totalDepositMin", required = false) totalDepositMin: BigDecimal?,
            @RequestParam("totalDepositMax", required = false) totalDepositMax: BigDecimal?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("lastDepositTimeMin", required = false) lastDepositTimeMin: LocalDate?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("lastDepositTimeMax", required = false) lastDepositTimeMax: LocalDate?,
            @RequestParam("totalDepositCountMin", required = false) totalDepositCountMin: Int?,
            @RequestParam("totalDepositCountMax", required = false) totalDepositCountMax: Int?,

            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("registerTimeMin", required = false) registerTimeMin: LocalDate?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("registerTimeMax", required = false) registerTimeMax: LocalDate?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("lastLoginTimeMin", required = false) lastLoginTimeMin: LocalDate?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("lastLoginTimeMax", required = false) lastLoginTimeMax: LocalDate?,

            @RequestParam("loginCountMin", required = false) loginCountMin: Int?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("lastSaleTimeMin", required = false) lastSaleTimeMin: LocalDate?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("lastSaleTimeMax", required = false) lastSaleTimeMax: LocalDate?,
            @RequestParam("saleCountMin", required = false) saleCountMin: Int?,
            @RequestParam("saleCountMax", required = false) saleCountMax: Int?,
            @RequestParam("sortBy", required = false, defaultValue = "0") sortBy: Int
    ): List<MemberInfoValue.MemberInfoVo> {

        val current = this.current()

        val saleId = if (current.role == Role.Sale) current.id else null

        val sortByStr = when (sortBy) {
            0 -> "member_id desc"
            1 -> "register_time desc"
            2 -> "last_deposit_time desc"
            3 -> "last_sale_time desc"
            4 -> "next_call_time asc"
            else -> "member_id desc"
        }

        // 查询
        val query = MemberInfoValue.MemberInfoQuery(bossId = current.bossId, clientId = current.clientId, saleId = saleId, totalDepositMin = totalDepositMin, totalDepositMax = totalDepositMax,
                lastDepositTimeMin = lastDepositTimeMin, lastDepositTimeMax = lastDepositTimeMax, totalDepositCountMin = totalDepositCountMin,
                totalDepositCountMax = totalDepositCountMax, registerTimeMin = registerTimeMin, registerTimeMax = registerTimeMax, lastLoginTimeMin = lastLoginTimeMin,
                lastLoginTimeMax = lastLoginTimeMax, loginCountMin = loginCountMin, loginCountMax = null, lastSaleTimeMin = lastSaleTimeMin, lastSaleTimeMax = lastSaleTimeMax,
                saleCountMin = saleCountMin, saleCountMax = saleCountMax, sortBy = sortByStr, username = username)
        val infoList = memberInfoService.list(query)
        if (infoList.isEmpty()) return emptyList()

        val memberIds = infoList.map { it.memberId }

        // 用户列表
        val memberQuery = MemberQuery(ids = memberIds)
        val members = memberService.list(memberQuery)
        val memberMap = members.map { it.id to it }.toMap()

        // 电销人员列表
        val sales = waiterService.findClientWaiters(clientId = current.clientId).filter { it.role == Role.Sale }
        val saleMap = sales.map { it.id to it }.toMap()

        // 返回数据
        return infoList.map { info ->
            val member = memberMap[info.memberId]
            val phone = member?.phone ?: "-"
            val name = member?.name ?: "-"

            val sale = saleMap[info.saleId]
            val saleUsername = sale?.username ?: "-"

            MemberInfoValue.MemberInfoVo(saleId = info.saleId, saleUsername = saleUsername, memberId = info.memberId, username = info.username,
                    totalDeposit = info.totalDeposit, lastDepositTime = info.lastDepositTime, totalDepositCount = info.totalDepositCount,
                    totalWithdraw = info.totalWithdraw, lastWithdrawTime = info.lastWithdrawTime, totalWithdrawCount = info.totalWithdrawCount,
                    registerTime = info.registerTime, lastLoginTime = info.lastLoginTime, loginCount = info.loginCount, lastSaleTime = info.lastSaleTime,
                    saleCount = info.saleCount, phone = phone, name = name, nextCallTime = info.nextCallTime)
        }
    }

    @GetMapping("/saleLog")
    override fun saleLogList(
            @RequestParam("saleId", required = false) saleId: Int?,
            @RequestParam("memberId") memberId: Int
    ): List<SaleLog> {
        val current = this.current()

        val tSaleId = saleId ?: current.id

        val query = SaleLogValue.SaleLogQuery(bossId = current.bossId, clientId = current.clientId, saleId = tSaleId, memberId = memberId)
        return saleLogService.list(query)
    }

    @PostMapping("/saleLog")
    override fun saleLog(
            @RequestParam("saleId", required = false) saleId: Int?,
            @RequestBody saleLogCo: SaleLogValue.SaleLogCo
    ) {

        val current = this.current()

        val tSaleId = saleId ?: current.id

        saleLogService.create(co = saleLogCo.copy(bossId = current.bossId, clientId = current.clientId, saleId = tSaleId))
    }

    @GetMapping("/deposit/history")
    override fun queryDepositHistory(
            @RequestParam("saleUsername", required = false) saleUsername: String?,
            @RequestParam("username", required = false) username: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate
    ): List<MemberReportValue.SaleMemberReportVo> {

        val user = this.current()

        val saleId = if (user.role == Role.Sale) user.id else null

        val query = MemberReportValue.CollectQuery(bossId = user.bossId, clientId = user.clientId, username = username, saleId = saleId, startDate = startDate,
                endDate = endDate)
        val day = "$startDate~$endDate"
        return memberDailyReportDao.collect(query).map {
            MemberReportValue.SaleMemberReportVo(day = day, memberId = it.memberId, username = it.username, depositCount = it.depositCount,
                    depositAmount = it.depositAmount, thirdPayCount = it.thirdPayCount, thirdPayAmount = it.thirdPayAmount)
        }
    }

    @GetMapping("/report/month")
    override fun monthReport(
            @RequestParam("saleUsername", required = false) saleUsername: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate
    ): List<SaleMonthReport> {
        val current = this.current()

        val saleId = if (current.role == Role.Sale) current.id else null

        val query = SaleMonthReportValue.SaleMonthReportQuery(bossId = current.bossId, clientId = current.clientId, startDate = startDate, endDate = endDate,
                saleUsername = saleUsername, saleId = saleId)
        return saleMonthReportDao.list(query)
    }

    @GetMapping("/report/daily")
    override fun dailyReport(
            @RequestParam("saleUsername", required = false) saleUsername: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate
    ): List<SaleDailyReport> {

        val current = this.current()

        val saleId = if (current.role == Role.Sale) current.id else null

        val query = SaleDailyReportValue.SaleDailyReportQuery(bossId = current.bossId, clientId = current.clientId, startDate = startDate, endDate = endDate,
                saleUsername = saleUsername, saleId = saleId)
        return saleDailyReportDao.list(query)
    }
}
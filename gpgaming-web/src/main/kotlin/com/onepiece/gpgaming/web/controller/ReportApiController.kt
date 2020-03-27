package com.onepiece.gpgaming.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.value.database.ClientReportQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.beans.value.database.PromotionDailyReportValue
import com.onepiece.gpgaming.beans.value.internet.web.MemberPlatformReportWebVo
import com.onepiece.gpgaming.beans.value.internet.web.MemberReportWebVo
import com.onepiece.gpgaming.beans.value.internet.web.PromotionReportValue
import com.onepiece.gpgaming.beans.value.internet.web.ReportValue
import com.onepiece.gpgaming.beans.value.internet.web.TransferOrderValue
import com.onepiece.gpgaming.core.service.ClientDailyReportService
import com.onepiece.gpgaming.core.service.ClientPlatformDailyReportService
import com.onepiece.gpgaming.core.service.I18nContentService
import com.onepiece.gpgaming.core.service.MemberDailyReportService
import com.onepiece.gpgaming.core.service.MemberPlatformDailyReportService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.PromotionDailyReportService
import com.onepiece.gpgaming.core.service.PromotionPlatformDailyReportService
import com.onepiece.gpgaming.core.service.PromotionService
import com.onepiece.gpgaming.core.service.ReportService
import com.onepiece.gpgaming.core.service.TransferOrderService
import com.onepiece.gpgaming.web.controller.basic.BasicController
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
        private val reportService: ReportService,
        private val promotionDailyReportService: PromotionDailyReportService,
        private val promotionPlatformDailyReportService: PromotionPlatformDailyReportService,
        private val transferOrderService: TransferOrderService,
        private val promotionService: PromotionService,
        private val i18nContentService: I18nContentService,
        private val objectMapper: ObjectMapper
) : BasicController(), ReportApi {

    private fun <T> includeToday(endDate: LocalDate, function: () -> List<T>): List<T> {
        val isToday = LocalDate.now().plusDays(1).until(endDate, ChronoUnit.DAYS) >= 0
        return if (isToday) {
            function()
        } else emptyList()
    }


    @GetMapping("/member/platform")
    override fun memberPlatformDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam(value = "memberId") memberId: Int
    ): ReportValue.MemberTotalReport {

        val clientId = getClientId()
//        val memberId = memberService.findByUsername(username)?.id?: return emptyList()

        val query = MemberReportQuery(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate)
        val history = memberPlatformDailyReportService.query(query)

        //查询今天的
        val todayData = this.includeToday(endDate) {
            reportService.startMemberPlatformDailyReport(memberId = memberId, startDate = LocalDate.now())
        }

        val data = history.plus(todayData)
        if (data.isEmpty()) return ReportValue.MemberTotalReport.empty()

        val ids = data.map { it.memberId }.toList()
        val members = memberService.findByIds(ids).map { it.id to it }.toMap()

        val list = data.map {
            val member = members[it.memberId] ?: error(OnePieceExceptionCode.DATA_FAIL)
            with(it) {
                MemberPlatformReportWebVo(day = day, clientId = clientId, memberId = member.id, username = member.username, platform = platform,
                        transferIn = transferIn, transferOut = transferOut)
            }
        }.sortedByDescending { it.day }

        return ReportValue.MemberTotalReport(list)
    }

    @GetMapping("/member")
    override fun memberDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam(value = "username", required = false) username: String?
    ): ReportValue.MemberTotalDetailReport {
        val clientId = getClientId()

        val memberId = memberService.findByUsername(clientId, username)?.id

        val query = MemberReportQuery(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate)
        val history = memberDailyReportService.query(query)

        //查询今天的
        val todayData = this.includeToday(endDate) {
            reportService.startMemberReport(memberId = memberId, startDate = LocalDate.now())
        }

        val data = history.plus(todayData)
        if (data.isEmpty()) return ReportValue.MemberTotalDetailReport(emptyList())

        val ids = data.map { it.memberId }.toList()
        val members = memberService.findByIds(ids).map { it.id to it }.toMap()

        val list =  data.map {
            val member = members[it.memberId] ?: error(OnePieceExceptionCode.DATA_FAIL)
            with(it) {
                MemberReportWebVo(day = day, clientId = clientId, memberId = member.id, username = member.username,
                        transferIn = transferIn, transferOut = transferOut, depositMoney = depositMoney,
                        withdrawMoney = withdrawMoney, artificialMoney = artificialMoney, artificialCount = artificialCount)
            }
        }.sortedByDescending { it.day }

        return ReportValue.MemberTotalDetailReport(list)

    }

    @GetMapping("/client/platform")
    override fun clientPlatformDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): ReportValue.CPTotalReport {
        val clientId = getClientId()

        val query = ClientReportQuery(clientId = clientId, startDate = startDate, endDate = endDate)

        //查询今天的
        val todayData = this.includeToday(endDate) {
            reportService.startClientPlatformReport(clientId = clientId, startDate = LocalDate.now())
        }

        val data = clientPlatformDailyReportService.query(query).plus(todayData).sortedByDescending { it.day }
        return ReportValue.CPTotalReport(data)
    }

    @GetMapping("/client")
    override fun clientDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): ReportValue.CTotalReport {
        val clientId = getClientId()

        val query = ClientReportQuery(clientId = clientId, startDate = startDate, endDate = endDate)

        //查询今天的
        val todayData = this.includeToday(endDate) {
            reportService.startClientReport(clientId = clientId, startDate = LocalDate.now())
        }

        val data = clientDailyReportService.query(query).plus(todayData).sortedByDescending { it.day }
        return ReportValue.CTotalReport(data)
    }

    @GetMapping("/promotion")
    override fun promotionDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): ReportValue.PromotionTotalReport {
        val query = PromotionDailyReportValue.Query(clientId = current().clientId, startDate = startDate, endDate = endDate)
        val list =  promotionDailyReportService.query(query)

        if (list.isEmpty()) return ReportValue.PromotionTotalReport(emptyList())

        val promotions = promotionService.all(clientId = getClientId()).map { it.id to it }.toMap()

        val i18nMap = i18nContentService.getConfigType(clientId = getClientId(), configType = I18nConfig.Promotion)
                .filter { it.language == Language.EN }
                .map { it.configId to it }
                .toMap()

        val data = list.map {
            with(it) {
                val promotionTitle = if (i18nMap[promotionId] != null) {
                    (i18nMap[promotionId]?.getII18nContent(objectMapper = objectMapper) as I18nContent.PromotionI18n).title
                } else ""
                PromotionReportValue.PromotionReportVo(clientId = clientId, day = day, promotionId = promotionId,
                        promotionAmount = promotionAmount, createdTime = createdTime, status = status,
                        promotionPlatforms = promotions[promotionId]?.platforms?.joinToString(separator = ",")?: "",
                        promotionTitle = promotionTitle)
            }
        }

        return ReportValue.PromotionTotalReport(data)

    }

    @GetMapping("/promotion/platform")
    override fun promotionPlatformDaily(
            @RequestParam("promotionId") promotionId: Int,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): ReportValue.PromotionCTotalReport {
        val query = PromotionDailyReportValue.PlatformQuery(clientId = current().clientId, promotionId = promotionId, startDate = startDate, endDate = endDate)
        val data = promotionPlatformDailyReportService.query(query)
        return ReportValue.PromotionCTotalReport(data)
    }

    @GetMapping("/promotion/detail")
    override fun promotionDetail(
            @RequestParam("promotionId", required = false) promotionId: Int?,
            @RequestParam("username", required = false) username: String?,
            @RequestParam("sortBy") sortBy: String,
            @RequestParam("desc") desc: Boolean
    ): ReportValue.PromotionMTotalReport {

        when (sortBy) {
            "created_time",
            "updated_time",
            "money",
            "promotion_amount" -> {

            }
            else -> error(OnePieceExceptionCode.SYSTEM)
        }

        val dbSort = "$sortBy ${if (desc) "desc" else "asc"}"

        val query = TransferOrderValue.Query(clientId = this.getClientId(), promotionId = promotionId, from = null, sortBy = dbSort, username = username,
                memberId = null)
        val data =  transferOrderService.query(query)

        return ReportValue.PromotionMTotalReport(data)
    }
}
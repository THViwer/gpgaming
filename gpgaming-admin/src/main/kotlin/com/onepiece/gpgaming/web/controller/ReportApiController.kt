package com.onepiece.gpgaming.web.controller

import com.alibaba.excel.EasyExcel
import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.MemberAnalysisSort
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.value.database.ClientReportQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
import com.onepiece.gpgaming.beans.value.database.PromotionDailyReportValue
import com.onepiece.gpgaming.beans.value.internet.web.ClientReportExcelVo
import com.onepiece.gpgaming.beans.value.internet.web.MemberPlatformReportWebVo
import com.onepiece.gpgaming.beans.value.internet.web.MemberReportWebVo
import com.onepiece.gpgaming.beans.value.internet.web.MemberValue
import com.onepiece.gpgaming.beans.value.internet.web.PromotionReportValue
import com.onepiece.gpgaming.beans.value.internet.web.ReportValue
import com.onepiece.gpgaming.beans.value.internet.web.TransferOrderValue
import com.onepiece.gpgaming.core.dao.MemberDailyReportDao
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
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.math.BigDecimal
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
        private val objectMapper: ObjectMapper,
        private val memberDailyReportDao: MemberDailyReportDao
) : BasicController(), ReportApi {

    private val log = LoggerFactory.getLogger(ReportApiController::class.java)

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

        val query = MemberReportQuery(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate, minPromotionMoney = null,
                minBackwaterMoney = null, current = 0, size = 10000)
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

    @GetMapping("/analysis")
    override fun analysis(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate", required = true) startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate", required = true) endDate: LocalDate,
            @RequestParam(value = "sort", required = true) sort: MemberAnalysisSort,
            @RequestParam(value = "size", required = true) size: Int
    ): List<MemberReportValue.AnalysisVo> {


        val query = MemberReportValue.AnalysisQuery(clientId = this.getClientId(), startDate = startDate, endDate = endDate,
                sort = sort, size = size)
        val list = memberDailyReportDao.analysis(query)
        if (list.isEmpty()) return emptyList()

        val ids = list.map { it.memberId }.toList()
        val members = memberService.findByIds(ids = ids).map { it.id to it }.toMap()

        return list.map {
            it.copy(username = members[it.memberId]?.username?: "")
        }

    }

    @GetMapping("/member")
    override fun memberDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam("minBackwaterMoney",  required = false) minBackwaterMoney: BigDecimal?,
            @RequestParam("minPromotionMoney",  required = false) minPromotionMoney: BigDecimal?,
            @RequestParam("current") current: Int,
            @RequestParam("size") size: Int
    ): ReportValue.MemberTotalDetailReport {
        val clientId = getClientId()

        val memberId = memberService.findByUsername(clientId, username)?.id

        val query = MemberReportQuery(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate,
                minBackwaterMoney = minBackwaterMoney, minPromotionMoney = minPromotionMoney, current = current, size = size)

        val total  = memberDailyReportService.total(query)
        val history = memberDailyReportService.query(query)

        //查询今天的
//        val todayData = this.includeToday(endDate) {
//            reportService.startMemberReport(memberId = memberId, startDate = LocalDate.now())
//        }.filter {
//            it.backwaterMoney >= minBackwaterMoney ?: BigDecimal.ZERO
//                    && it.promotionMoney >= minPromotionMoney ?:  BigDecimal.ZERO
//        }
//
//        val data = history.plus(todayData)
        val data = history
        if (data.isEmpty()) {
            val emptyTotal = MemberReportValue.MemberReportTotal(count = 0,  totalMWin = BigDecimal.ZERO, totalBet = BigDecimal.ZERO, transferIn = BigDecimal.ZERO,
                    transferOut = BigDecimal.ZERO, totalDepositCount = 0, totalDepositMoney = BigDecimal.ZERO, totalWithdrawCount = 0, totalWithdrawMoney = BigDecimal.ZERO,
                    totalArtificialCount = 0, totalArtificialMoney = BigDecimal.ZERO, totalThirdPayCount = 0, totalThirdPayMoney = BigDecimal.ZERO, totalBackwaterMoney = BigDecimal.ZERO,
                    totalPromotionMoney = BigDecimal.ZERO)
            return ReportValue.MemberTotalDetailReport(data = emptyList(), memberReportTotal = emptyTotal)
        }
//
        val ids = data.map { it.memberId }.toList()
        val members = memberService.findByIds(ids).map { it.id to it }.toMap()

        val list = history.mapNotNull {

            try {
                val member = members[it.memberId] ?: error(OnePieceExceptionCode.DATA_FAIL)
                with(it) {
                    MemberReportWebVo(day = day, clientId = clientId, memberId = member.id, username = member.username,
                            transferIn = transferIn, transferOut = transferOut, depositMoney = depositMoney,
                            withdrawMoney = withdrawMoney, artificialMoney = artificialMoney, artificialCount = artificialCount,
                            settles = it.settles, totalMWin = it.totalMWin, totalBet = it.totalBet, thirdPayCount = thirdPayCount,
                            thirdPayMoney = thirdPayMoney,  backwaterMoney = it.backwaterMoney,
                            promotionMoney = it.promotionMoney)
                }
            } catch (e: Exception) {
                log.error("", e)
                null
            }
        }.sortedByDescending { it.day }

        return ReportValue.MemberTotalDetailReport(data = list,  memberReportTotal = total)

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
//        val todayData = this.includeToday(endDate) {
//            reportService.startClientReport(clientId = clientId, startDate = LocalDate.now())
//        }

        val data = clientDailyReportService.query(query)//.plus(todayData).sortedByDescending { it.day }
        return ReportValue.CTotalReport(data)
    }


    @GetMapping("/client/excel")
    override fun clientDailyExcel(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ) {

        val response = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).response!!
        val name = "client_report_${startDate.toString().replace("-", "")}_${endDate.toString().replace("_", "")}"

        response.contentType = "application/vnd.ms-excel";
        response.characterEncoding = "utf-8";
        response.setHeader("Content-disposition", "attachment;filename=$name.xlsx")

        val data = this.clientDaily(startDate = startDate, endDate = endDate).data.map {
            with(it) {
                ClientReportExcelVo(day = day.toString(),totalBet = totalBet, totalMWin = totalMWin, transferIn = transferIn, transferOut = transferOut, depositMoney = depositMoney,
                depositCount = depositCount, depositSequence = depositSequence, thirdPayMoney = thirdPayMoney, thirdPayCount = thirdPayCount, thirdPaySequence = thirdPaySequence,
                promotionAmount = promotionAmount, withdrawMoney = withdrawMoney, withdrawCount = withdrawCount, artificialMoney = artificialMoney, artificialCount = artificialCount,
                backwaterMoney = backwaterMoney, newMemberCount = newMemberCount)
            }
        }

        EasyExcel.write(response.outputStream, ClientReportExcelVo::class.java).autoCloseStream(false).sheet("member").doWrite(data)

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
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
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
                memberId = null, startDate = startDate, endDate = endDate, filterPromotion = true)
        val data =  transferOrderService.query(query)

        return ReportValue.PromotionMTotalReport(data)
    }
}
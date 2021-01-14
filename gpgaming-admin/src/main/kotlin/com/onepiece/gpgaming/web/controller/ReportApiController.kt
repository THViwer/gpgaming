package com.onepiece.gpgaming.web.controller

import com.alibaba.excel.EasyExcel
import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.MemberAnalysisSort
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.value.database.ClientReportQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
import com.onepiece.gpgaming.beans.value.database.PromotionDailyReportValue
import com.onepiece.gpgaming.beans.value.internet.web.ClientReportExcelVo
import com.onepiece.gpgaming.beans.value.internet.web.MemberPlatformReportWebVo
import com.onepiece.gpgaming.beans.value.internet.web.MemberReportWebVo
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

        val query = MemberReportQuery(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate, minRebateAmount = null,
                minPromotionAmount = null, current = 0, size = 10000, agentId = null)
        val history = memberPlatformDailyReportService.query(query)

        //查询今天的
        val todayData = this.includeToday(endDate) {
            reportService.startMemberPlatformDailyReport(startDate = LocalDate.now())
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


    @GetMapping("/analysis", "/member/month")
    override fun analysis(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate", required = true) startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate", required = true) endDate: LocalDate,
            username: String?,
            @RequestParam(value = "sort", required = false, defaultValue = "DepositMax") sort: MemberAnalysisSort,
            @RequestParam(value = "size", required = true) size: Int
    ): List<MemberReportValue.AnalysisVo> {

        val clientId = this.current().clientId

        val memberId = if (!username.isNullOrBlank()) {
            memberService.findByUsername(clientId = clientId, username = username)?.id ?: return emptyList()
        } else null

        val query = MemberReportValue.AnalysisQuery(clientId = this.getClientId(), startDate = startDate, endDate = endDate,
                sort = sort, size = size, memberId = memberId)
        val list = memberDailyReportDao.analysis(query)
        if (list.isEmpty()) return emptyList()

        val ids = list.map { it.memberId }.toList()
        val members = memberService.findByIds(ids = ids).map { it.id to it }.toMap()

        return list.map {
            it.copy(username = members[it.memberId]?.username ?: "")
        }

    }

    @GetMapping("/member")
    override fun memberDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam("minRebateAmount", required = false) minRebateAmount: BigDecimal?,
            @RequestParam("minPromotionAmount", required = false) minPromotionAmount: BigDecimal?,
            @RequestParam("current") current: Int,
            @RequestParam("size") size: Int
    ): ReportValue.MemberTotalDetailReport {
        val clientId = getClientId()

        val memberId = memberService.findByUsername(clientId, username)?.id

        val query = MemberReportQuery(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate,
                minRebateAmount = minRebateAmount, minPromotionAmount = minPromotionAmount, current = current, size = size,
                agentId = null)

        val total = memberDailyReportService.total(query)
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
            val emptyTotal = MemberReportValue.MemberReportTotal(count = 0, payout = BigDecimal.ZERO, totalBet = BigDecimal.ZERO, transferIn = BigDecimal.ZERO,
                    transferOut = BigDecimal.ZERO, totalDepositCount = 0, totalDepositAmount = BigDecimal.ZERO, totalWithdrawCount = 0, totalWithdrawAmount = BigDecimal.ZERO,
                    totalArtificialCount = 0, totalArtificialAmount = BigDecimal.ZERO, totalThirdPayCount = 0, totalThirdPayAmount = BigDecimal.ZERO, totalRebateAmount = BigDecimal.ZERO,
                    totalPromotionAmount = BigDecimal.ZERO, betCount = 0)
            return ReportValue.MemberTotalDetailReport(data = emptyList(), memberReportTotal = emptyTotal)
        }
//
        val ids = data.map { it.memberId }.toList()
        val members = memberService.findByIds(ids).map { it.id to it }.toMap()

        // history處理
        val day = "${startDate}~${endDate}"

        val list = history.groupBy { it.memberId }.mapNotNull {

            try {

                val v = it.value
                val first = v.first()

                val member = members[first.memberId] ?: error(OnePieceExceptionCode.DATA_FAIL)

                val transferIn = v.sumByDouble { x -> x.transferIn.toDouble() }.toBigDecimal().setScale(2, 2)
                val transferOut = v.sumByDouble { x -> x.transferOut.toDouble() }.toBigDecimal().setScale(2, 2)
                val depositAmount = v.sumByDouble { x -> x.depositAmount.toDouble() }.toBigDecimal().setScale(2, 2)
                val depositCount = v.sumBy { x -> x.depositCount }
                val thirdPayAmount = v.sumByDouble { x -> x.thirdPayAmount.toDouble() }.toBigDecimal().setScale(2, 2)
                val thirdPayCount = v.sumBy { x -> x.thirdPayCount }
                val withdrawAmount = v.sumByDouble { x -> x.withdrawAmount.toDouble() }.toBigDecimal().setScale(2, 2)
                val withdrawCount = v.sumBy { x -> x.withdrawCount }
                val artificialAmount = v.sumByDouble { x -> x.artificialAmount.toDouble() }.toBigDecimal().setScale(2, 2)
                val artificialCount = v.sumBy { x -> x.artificialCount }
                val payout = v.sumByDouble { x -> x.payout.toDouble() }.toBigDecimal().setScale(2, 2)
                val totalBet = v.sumByDouble { x -> x.totalBet.toDouble() }.toBigDecimal().setScale(2, 2)
                val betCount = v.sumBy { x -> x.betCount }
                val rebateAmount = v.sumByDouble { x -> x.rebateAmount.toDouble() }.toBigDecimal().setScale(2, 2)
                val promotionAmount = v.sumByDouble { x -> x.promotionAmount.toDouble() }.toBigDecimal().setScale(2, 2)


                val settles = v.map { x -> x.settles }.reduce { a, b -> a.plus(b) }
                        .groupBy { y -> y.platform }
                        .map { z ->
                            val ss = z.value
                            val f1 = ss.first()

                            val bet = ss.sumByDouble { w -> w.bet.toDouble() }.toBigDecimal().setScale(2, 2)
                            val betCount = ss.sumBy { w -> w.betCount }
                            val validBet = ss.sumByDouble { w -> w.validBet.toDouble() }.toBigDecimal().setScale(2, 2)
                            val payout = ss.sumByDouble { w -> w.payout.toDouble() }.toBigDecimal().setScale(2, 2)
                            val rebate = ss.sumByDouble { w -> w.rebate.toDouble() }.toBigDecimal().setScale(2, 2)
                            val requirementBet = ss.sumByDouble { w -> w.requirementBet.toDouble() }.toBigDecimal().setScale(2, 2)
                            val totalIn = ss.sumByDouble { w -> w.totalIn.toDouble() }.toBigDecimal().setScale(2, 2)
                            val totalOut = ss.sumByDouble { w -> w.totalOut.toDouble() }.toBigDecimal().setScale(2, 2)

                            f1.copy(bet = bet, betCount = betCount, validBet = validBet, payout = payout, rebate = rebate, requirementBet = requirementBet, totalIn = totalIn,
                                    totalOut = totalOut)
                        }

                MemberReportWebVo(day = day, transferIn = transferIn, transferOut = transferOut, depositAmount = depositAmount, thirdPayAmount = thirdPayAmount,
                        thirdPayCount = thirdPayCount, withdrawAmount = withdrawAmount, artificialAmount = artificialAmount, artificialCount = artificialCount,
                        payout = payout, totalBet = totalBet, betCount = betCount, rebateAmount = rebateAmount, promotionAmount = promotionAmount, settles = settles,
                        clientId = member.clientId, username = member.username, memberId = member.id, phone = member.phone)
            } catch (e: Exception) {
                log.error("", e)
                null
            }
        }

//        val list = history.mapNotNull {
//
//            try {
//                val member = members[it.memberId] ?: error(OnePieceExceptionCode.DATA_FAIL)
//                with(it) {
//                    MemberReportWebVo(day = day, clientId = clientId, memberId = member.id, username = member.username,
//                            transferIn = transferIn, transferOut = transferOut, depositAmount = depositAmount,
//                            withdrawAmount = withdrawAmount, artificialAmount = artificialAmount, artificialCount = artificialCount,
//                            settles = it.settles, payout = it.payout, totalBet = it.totalBet, thirdPayCount = thirdPayCount,
//                            thirdPayAmount = thirdPayAmount, rebateAmount = it.rebateAmount,
//                            promotionAmount = it.promotionAmount, phone = member.phone, betCount = it.betCount)
//                }
//            } catch (e: Exception) {
//                log.error("", e)
//                null
//            }
//        }.sortedByDescending { it.day }

        return ReportValue.MemberTotalDetailReport(data = list, memberReportTotal = total)

    }


    @GetMapping("/member/excel")
    override fun memberDailyExcel(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam("minRebateAmount", required = false) minRebateAmount: BigDecimal?,
            @RequestParam("minPromotionAmount", required = false) minPromotionAmount: BigDecimal?
    ) {

        val data = this.memberDaily(startDate = startDate, endDate = endDate, username = username, minRebateAmount = minRebateAmount,
                minPromotionAmount = minPromotionAmount, current = 0, size = 999999)
                .data

        val response = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).response!!
        val name = "member_report_${startDate.toString().replace("-", "")}_${endDate.toString().replace("_", "")}"

        response.contentType = "application/vnd.ms-excel";
        response.characterEncoding = "utf-8";
        response.setHeader("Content-disposition", "attachment;filename=$name.xlsx")
        EasyExcel.write(response.outputStream, MemberReportWebVo::class.java).autoCloseStream(false).sheet("member").doWrite(data)
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
            reportService.startClientPlatformReport(startDate = LocalDate.now())
        }

        val data = clientPlatformDailyReportService.query(query).plus(todayData).sortedByDescending { it.day }

        //10.27 新需要 显示不需要分日期 显示总日期
        val data1027 = data.groupBy { it.platform }.map {

            val dailyReports = it.value
            val first = dailyReports.first()

            val bet = dailyReports.sumByDouble { r -> r.bet.toDouble() }.toBigDecimal().setScale(2, 2)
            val payout = dailyReports.sumByDouble { r -> r.payout.toDouble() }.toBigDecimal().setScale(2, 2)
            val transferIn = dailyReports.sumByDouble { r -> r.transferIn.toDouble() }.toBigDecimal().setScale(2, 2)
            val transferOut = dailyReports.sumByDouble { r -> r.transferOut.toDouble() }.toBigDecimal().setScale(2, 2)
            val promotionAmount = dailyReports.sumByDouble { r -> r.promotionAmount.toDouble() }.toBigDecimal().setScale(2, 2)
            val activeCount = dailyReports.sumBy { r -> r.activeCount }

            first.copy(day = "$startDate~$endDate", bet = bet, payout = payout, transferIn = transferIn, transferOut = transferOut,
                    promotionAmount = promotionAmount, activeCount = activeCount)
        }
        return ReportValue.CPTotalReport(data1027)
    }


    @GetMapping("/client/member/report")
    override fun platformMemberDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam("platform") platform: Platform
    ): List<ReportValue.PlatformSettleVo> {

        val user = this.current()

        val memberQuery = MemberReportQuery(clientId = user.clientId, agentId = null, memberId = null, startDate = startDate, endDate = endDate,
                minPromotionAmount = null, minRebateAmount = null, current = 0, size = 999999)
        val list = memberDailyReportDao.query(memberQuery)

        return list.mapNotNull { report ->
            report.settles.firstOrNull { it.platform == platform }
                    ?.let { settle ->
                        ReportValue.PlatformSettleVo(memberId = report.memberId, platform = platform, username = report.username, bet = settle.bet, validBet = settle.validBet,
                                payout = settle.payout)
                    }
        }.sortedByDescending { it.bet }
                .groupBy { it.memberId }
                .map {

                    val first = it.value.first()


                    // 下注
                    val bet: BigDecimal = it.value.sumByDouble { x -> x.bet.toDouble() }.toBigDecimal().setScale(2, 2)

                    // 有效投注
                    val validBet: BigDecimal = it.value.sumByDouble { x -> x.validBet.toDouble() }.toBigDecimal().setScale(2, 2)

                    // 顾客盈利
                    val payout: BigDecimal = it.value.sumByDouble { x -> x.payout.toDouble() }.toBigDecimal().setScale(2, 2)

                    // 反水
                    val rebate: BigDecimal = it.value.sumByDouble { x -> x.rebate.toDouble() }.toBigDecimal().setScale(2, 2)

                    first.copy(bet = bet, validBet = validBet, payout = payout, rebate = rebate)
                }

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
                ClientReportExcelVo(day = day.toString(), totalBet = totalBet, payout = payout, transferIn = transferIn, transferOut = transferOut, depositAmount = depositAmount,
                        depositCount = depositCount, thirdPayAmount = depositAmount, thirdPayCount = thirdPayCount, promotionAmount = promotionAmount, withdrawAmount = withdrawAmount,
                        withdrawCount = withdrawCount, artificialAmount = artificialAmount, artificialCount = artificialCount, rebateAmount = rebateAmount, newMemberCount = newMemberCount)
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
        val list = promotionDailyReportService.query(query)

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
                        promotionPlatforms = promotions[promotionId]?.platforms?.joinToString(separator = ",") ?: "",
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
        val data = transferOrderService.query(query)

        return ReportValue.PromotionMTotalReport(data)
    }
}
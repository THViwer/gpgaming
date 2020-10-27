package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.MemberAnalysisSort
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
import com.onepiece.gpgaming.beans.value.internet.web.ReportValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal
import java.time.LocalDate

@Api(tags = ["report"], description = "报表管理")
interface ReportApi {

    @ApiOperation(tags = ["report"], value = "会员平台报表")
    fun memberPlatformDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam(value = "memberId") memberId: Int
    ): ReportValue.MemberTotalReport

    @ApiOperation(tags = ["report"], value = "会员报表")
    fun memberDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam("minRebateAmount",  required = false) minRebateAmount: BigDecimal?,
            @RequestParam("minPromotionAmount",  required = false) minPromotionAmount: BigDecimal?,
            @RequestParam("current") current: Int,
            @RequestParam("size") size: Int
    ): ReportValue.MemberTotalDetailReport

    @ApiOperation(tags = ["user"], value = "会员 -> 分析查询")
    fun analysis(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate", required = true) startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate", required = true) endDate: LocalDate,
            username: String?,
            @RequestParam(value = "sort", required = false, defaultValue = "DepositMax") sort: MemberAnalysisSort = MemberAnalysisSort.DepositMax,
            @RequestParam(value = "size", required = true) size: Int
    ): List<MemberReportValue.AnalysisVo>

    @ApiOperation(tags = ["report"], value = "厅主平台报表")
    fun clientPlatformDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): ReportValue.CPTotalReport

    @ApiOperation(tags = ["report"], value = "会员平台报表详情")
    fun platformMemberDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam("platform") platform:  Platform
    ): List<ReportValue.PlatformSettleVo>


    @ApiOperation(tags = ["report"], value = "厅主报表")
    fun clientDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): ReportValue.CTotalReport


    @ApiOperation(tags = ["user"], value = "厅主平台报表 -> 导出excel")
    fun clientDailyExcel(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "registerStartDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "registerEndDate") endDate: LocalDate
    )

    @ApiOperation(tags = ["report"], value = "优惠活动日报表")
    fun promotionDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): ReportValue.PromotionTotalReport

    @ApiOperation(tags = ["report"], value = "优惠活动日报表详情")
    fun promotionPlatformDaily(
            @RequestParam("promotionId") promotionId: Int,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): ReportValue.PromotionCTotalReport

    @ApiOperation(tags = ["report"], value = "优惠活动人员详情")
    fun promotionDetail(
            @RequestParam("promotionId", required = false) promotionId: Int?,
            @RequestParam("username", required = false) username: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam("sortBy") sortBy: String,
            @RequestParam("desc") desc: Boolean
    ): ReportValue.PromotionMTotalReport

}
package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.model.ClientPlatformDailyReport
import com.onepiece.gpgaming.beans.model.PromotionDailyReport
import com.onepiece.gpgaming.beans.model.PromotionPlatformDailyReport
import com.onepiece.gpgaming.beans.model.TransferOrder
import com.onepiece.gpgaming.beans.value.internet.web.MemberPlatformReportWebVo
import com.onepiece.gpgaming.beans.value.internet.web.MemberReportWebVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@Api(tags = ["report"], description = "报表管理")
interface ReportApi {

    @ApiOperation(tags = ["report"], value = "会员平台报表")
    fun memberPlatformDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam(value = "memberId") memberId: Int
    ): List<MemberPlatformReportWebVo>

    @ApiOperation(tags = ["report"], value = "会员报表")
    fun memberDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam(value = "username", required = false) username: String?
    ): List<MemberReportWebVo>

    @ApiOperation(tags = ["report"], value = "厅主平台报表")
    fun clientPlatformDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): List<ClientPlatformDailyReport>

    @ApiOperation(tags = ["report"], value = "厅主报表")
    fun clientDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): List<ClientDailyReport>

    @ApiOperation(tags = ["report"], value = "优惠活动日报表")
    fun promotionDaily(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): List<PromotionDailyReport>

    @ApiOperation(tags = ["report"], value = "优惠活动日报表详情")
    fun promotionPlatformDaily(
            @RequestParam("promotionId") promotionId: Int,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate
    ): List<PromotionPlatformDailyReport>

    @ApiOperation(tags = ["report"], value = "优惠活动人员详情")
    fun promotionDetail(
            @RequestParam("promotionId") promotionId: Int,
            @RequestParam("sortBy") sortBy: String,
            @RequestParam("desc") desc: Boolean
    ): List<TransferOrder>

}
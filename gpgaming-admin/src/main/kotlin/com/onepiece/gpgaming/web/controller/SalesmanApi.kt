package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.model.SaleDailyReport
import com.onepiece.gpgaming.beans.model.SaleLog
import com.onepiece.gpgaming.beans.model.SaleMonthReport
import com.onepiece.gpgaming.beans.value.database.MemberInfoValue
import com.onepiece.gpgaming.beans.value.database.SaleLogValue
import com.onepiece.gpgaming.beans.value.internet.web.SalesmanValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.math.BigDecimal
import java.time.LocalDate

@Api(tags = ["sale"], description = "电销")
interface SalesmanApi {

    @ApiOperation(tags = ["sale"], value = "电销 -> 个人信息")
    fun info(): SalesmanValue.SaleInfo

    @ApiOperation(tags = ["sale"], value = "电销 -> 会员列表")
    fun myMemberList(
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
    ): List<MemberInfoValue.MemberInfoVo>

    @ApiOperation(tags = ["sale"], value = "电销 -> 记录列表")
    fun saleLogList(
            @RequestParam("saleId", required = false) saleId: Int?,
            @RequestParam("memberId") memberId: Int
    ): List<SaleLog>

    @ApiOperation(tags = ["sale"], value = "电销 -> 记录")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun saleLog(
            @RequestParam("saleId", required = false) saleId: Int?,
            @RequestBody saleLogCo: SaleLogValue.SaleLogCo
    )

    @ApiOperation(tags = ["sale"], value = "电销 -> 月报表")
    fun monthReport(
            @RequestParam("saleUsername", required = false) saleUsername: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate
    ): List<SaleMonthReport>

    @ApiOperation(tags = ["sale"], value = "电销 -> 日报表")
    fun dailyReport(
            @RequestParam("saleUsername", required = false) saleUsername: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate
    ): List<SaleDailyReport>

}
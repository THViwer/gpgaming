package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.model.ClientDailyReport
import com.onepiece.treasure.beans.model.ClientPlatformDailyReport
import com.onepiece.treasure.beans.value.internet.web.MemberPlatformReportWebVo
import com.onepiece.treasure.beans.value.internet.web.MemberReportWebVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@Api(tags = ["report"], description = " ")
interface ReportApi {

    @ApiOperation(tags = ["report"], value = "会员平台报表")
    fun memberPlatformDaily(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("startTime") startDate: LocalDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("startTime") endDate: LocalDate,
            @RequestParam(value = "username", required = false) username: String?
    ): List<MemberPlatformReportWebVo>

    @ApiOperation(tags = ["report"], value = "会员报表")
    fun memberDaily(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("startTime") startDate: LocalDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("startTime") endDate: LocalDate,
            @RequestParam(value = "username", required = false) username: String?
    ): List<MemberReportWebVo>

    @ApiOperation(tags = ["report"], value = "厅主平台报表")
    fun clientPlatformDaily(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("startTime") startDate: LocalDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("startTime") endDate: LocalDate
    ): List<ClientPlatformDailyReport>

    @ApiOperation(tags = ["report"], value = "厅主报表")
    fun clientDaily(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("startTime") startDate: LocalDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("startTime") endDate: LocalDate
    ): List<ClientDailyReport>

}
package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.model.MarketDailyReport
import com.onepiece.gpgaming.beans.value.database.MarketingValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDate

@Api(tags = ["market"], description = " ")
interface MarketApi {

    @ApiOperation(tags = ["market"], value = "营销 -> 列表")
    fun list(): List<MarketingValue.MarketVo>

    @ApiOperation(tags = ["market"], value = "营销 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun createCo(@RequestBody co: MarketingValue.MarketingCo)

    @ApiOperation(tags = ["market"], value = "营销 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun marketUpdate(@RequestBody uo: MarketingValue.MarketingUo)


    @ApiOperation(tags = ["market"], value = "营销 -> 注册短信提示")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun regMsgTemplate(@RequestBody req: MarketingValue.RegisterSmsTemplateReq)

    @ApiOperation(tags = ["market"], value = "营销 -> 报表")
    fun marketReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate", required = true) startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate", required = true) endDate: LocalDate
    ): List<MarketDailyReport>
}
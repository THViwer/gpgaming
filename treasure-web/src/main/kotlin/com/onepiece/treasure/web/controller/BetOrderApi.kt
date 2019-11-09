package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Platform
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@Api(tags = ["bet"], description = " ")
interface BetOrderApi {

    @ApiOperation(tags = ["bet"], value = "下注订单列表")
    fun bets(
            @RequestHeader("platform") platform: Platform,
            @RequestParam("username") username: String,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate): Any


}
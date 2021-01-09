package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.BetOrder
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDateTime

@Api(tags = ["bet"], description = "平台订单")
interface BetOrderApi {

    @ApiOperation(tags = ["bet"], value = "下注订单列表")
    fun bets(
            @RequestHeader("platform") platform: Platform,
            @RequestParam("username") username: String,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") endTime: LocalDateTime
    ): Any

    @ApiOperation(tags = ["bet"], value = "下注订单最后500条")
    fun last500(@RequestParam("username") username: String): List<BetOrder>

    @ApiOperation(tags = ["bet"], value = "下注订单导出")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun lastExcel(@RequestParam("username") username: String)

}
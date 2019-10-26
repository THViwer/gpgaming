package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.enums.WithdrawState
import com.onepiece.treasure.beans.value.internet.web.DepositUoReq
import com.onepiece.treasure.beans.value.internet.web.DepositVo
import com.onepiece.treasure.beans.value.internet.web.WithdrawUoReq
import com.onepiece.treasure.beans.value.internet.web.WithdrawVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDateTime

@Api(tags = ["cash"], description = " ")
interface CashOrderApi {

    @ApiOperation(tags = ["cash"], value = "deposit -> query")
    fun deposit(
            @RequestParam(value = "state", required = false) state: DepositState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") endTime: LocalDateTime
    ): List<DepositVo>

    @ApiOperation(tags = ["cash"], value = "deposit -> lock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun tryLock(@RequestParam("orderId") orderId: String)

    @ApiOperation(tags = ["cash"], value = "deposit -> check")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun check(@RequestBody depositUoReq: DepositUoReq)

//    @ApiOperation(tags = ["cash"], value = "deposit -> enforcement")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    fun enforcement(@RequestBody depositUoReq: DepositUoReq)

    @ApiOperation(tags = ["cash"], value = "withdraw -> query")
    fun withdraw(
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") endTime: LocalDateTime
    ): List<WithdrawVo>

    @ApiOperation(tags = ["cash"], value = "withdraw -> lock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun withdrawLock(@RequestParam("orderId") orderId: String)

    @ApiOperation(tags = ["cash"], value = "withdraw -> check")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun withdrawCheck(@RequestBody withdrawUoReq: WithdrawUoReq)

}
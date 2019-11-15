package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.enums.WithdrawState
import com.onepiece.treasure.beans.value.internet.web.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDateTime

@Api(tags = ["cash"], description = "现金管理")
interface CashOrderApi {

    @ApiOperation(tags = ["cash"], value = "充值 -> 列表")
    fun deposit(
            @RequestParam(value = "state", required = false) state: DepositState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") endTime: LocalDateTime
    ): List<DepositVo>

    @ApiOperation(tags = ["cash"], value = "充值 -> 锁定")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun tryLock(@RequestParam("orderId") orderId: String)

    @ApiOperation(tags = ["cash"], value = "充值 -> 审核")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun check(@RequestBody depositUoReq: DepositUoReq)

//    @ApiOperation(tags = ["cash"], value = "充值 -> 强行添加金额")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    fun artificial(@RequestBody artificialCoReq: ArtificialCoReq)

    @ApiOperation(tags = ["cash"], value = "取款 -> 列表")
    fun withdraw(
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") endTime: LocalDateTime
    ): List<WithdrawVo>

    @ApiOperation(tags = ["cash"], value = "取款 -> 锁定")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun withdrawLock(@RequestParam("orderId") orderId: String)

    @ApiOperation(tags = ["cash"], value = "取款 -> 审核")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun withdrawCheck(@RequestBody withdrawUoReq: WithdrawUoReq)

}
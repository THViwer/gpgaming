package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.enums.WithdrawState
import com.onepiece.treasure.beans.value.internet.web.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDateTime

@Api(tags = ["cash"], description = "现金管理")
interface CashOrderApi {


    @ApiOperation(tags = ["cash"], value = "出入款 -> 列表")
    @Deprecated(message = "请使用出入款接口")
    fun check(): List<CashValue.CheckOrderVo>

    @ApiOperation(tags = ["cash"], value = "出入款 -> 锁定")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Deprecated(message = "请使用出入款接口")
    fun checkLock(@RequestBody req: CashValue.CheckOrderLockReq)

    @ApiOperation(tags = ["cash"], value = "出入款 -> 审核")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Deprecated(message = "请使用出入款接口")
    fun check(@RequestBody req: CashValue.CheckOrderReq)






    @ApiOperation(tags = ["cash"], value = "充值 -> 审核列表")
    fun deposit(): List<DepositVo>

    @ApiOperation(tags = ["cash"], value = "充值 -> 历史")
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

    @ApiOperation(tags = ["cash"], value = "充值 -> 人工提存")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun artificial(@RequestBody artificialCoReq: ArtificialCoReq)

    @ApiOperation(tags = ["cash"], value = "取款 -> 审核列表")
    fun withdraw(): List<WithdrawVo>

    @ApiOperation(tags = ["cash"], value = "取款 -> 历史")
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

    @ApiOperation(tags = ["cash"], value = "转账 -> 查询优惠转账")
    fun query(
            @RequestParam("promotionId") promotionId: Int
    ): List<TransferOrderValue.TransferOrderVo>

}
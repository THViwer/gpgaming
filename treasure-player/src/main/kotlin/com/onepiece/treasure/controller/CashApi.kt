package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.WithdrawState
import com.onepiece.treasure.beans.value.internet.web.ClientBankVo
import com.onepiece.treasure.beans.value.internet.web.DepositVo
import com.onepiece.treasure.beans.value.internet.web.WithdrawVo
import com.onepiece.treasure.controller.value.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.math.BigDecimal
import java.time.LocalDateTime

@Api(tags = ["cash"], description = " ")
interface CashApi {

    @ApiOperation(tags = ["cash"], value = "银行列表")
    fun banks(): List<MemberBankVo>

    @ApiOperation(tags = ["cash"], value = "银行创建")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun bankCreate(@RequestBody memberBankCoReq: MemberBankCoReq)

    @ApiOperation(tags = ["cash"], value = "银行修改")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun bankUpdate(@RequestBody memberBankUoReq: MemberBankUoReq)

    @ApiOperation(tags = ["cash"], value = "厅主银行卡列表")
    fun clientBanks(): List<ClientBankVo>

    @ApiOperation(tags = ["cash"], value = "充值列表")
    fun deposit(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: DepositState?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") endTime: LocalDateTime,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<DepositVo>

    @ApiOperation(tags = ["cash"], value = "充值")
    fun deposit(@RequestBody depositCoReq: DepositCoReq): CashDepositResp

    @ApiOperation(tags = ["cash"], value = "取款列表")
    fun withdraw(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") endTime: LocalDateTime,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<WithdrawVo>

    @ApiOperation(tags = ["cash"], value = "取款")
    fun withdraw(@RequestBody withdrawCoReq: WithdrawCoReq): CashWithdrawResp

    @ApiOperation(tags = ["cash"], value = "转账")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun transfer(@RequestBody cashTransferReq: CashTransferReq)

    @ApiOperation(tags = ["cash"], value = "查询余额")
    fun balance(@RequestHeader("platform") platform: Platform):BigDecimal

}
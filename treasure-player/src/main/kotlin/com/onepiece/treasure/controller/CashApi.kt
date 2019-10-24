package com.onepiece.treasure.controller

import com.onepiece.treasure.core.model.enums.DepositState
import com.onepiece.treasure.controller.value.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDateTime

@Api(tags = ["cash"], description = " ")
interface CashApi {

    @ApiOperation(tags = ["cash"], value = "bank")
    fun banks(): List<MemberBankVo>

    @ApiOperation(tags = ["cash"], value = "bank")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun bankCreate(@RequestBody memberBankCo: MemberBankCo)

    @ApiOperation(tags = ["cash"], value = "bank")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun bankUpdate(@RequestBody memberBankUo: MemberBankUo)

    @ApiOperation(tags = ["cash"], value = "topup")
    fun topUp(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: DepositState?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") endTime: LocalDateTime,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): CashDepositPage

    @ApiOperation(tags = ["cash"], value = "topup")
    fun topUp(@RequestBody cashTopUpReq: CashDepositReq): CashDepositResp

    @ApiOperation(tags = ["cash"], value = "withdraw")
    fun withdraw(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: DepositState?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startCreatedTime") startCreatedTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endCreatedTime") endCreatedTime: LocalDateTime,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): CashWithdrawPage

    @ApiOperation(tags = ["cash"], value = "withdraw")
    fun withdraw(@RequestBody cashWithdrawReq: CashWithdrawReq): CashWithdrawResp

    @ApiOperation(tags = ["cash"], value = "transfer")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun transfer(@RequestBody cashTransferReq: CashTransferReq)

}
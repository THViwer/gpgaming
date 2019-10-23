package com.onepiece.treasure.web.controller

import com.onepiece.treasure.account.model.enums.TopUpState
import com.onepiece.treasure.account.model.enums.WithdrawState
import com.onepiece.treasure.web.controller.value.TopUpUo
import com.onepiece.treasure.web.controller.value.TopUpVo
import com.onepiece.treasure.web.controller.value.WithdrawUo
import com.onepiece.treasure.web.controller.value.WithdrawVo
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

    @ApiOperation(tags = ["cash"], value = "topup -> query")
    fun topup(
            @RequestParam(value = "state", required = false) state: TopUpState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("minCreatedTime") minCreatedTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("maxCreatedTime") maxCreatedTime: LocalDateTime
    ): List<TopUpVo>

    @ApiOperation(tags = ["cash"], value = "topup -> check")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun check(@RequestBody topUpUo: TopUpUo)

    @ApiOperation(tags = ["cash"], value = "topup -> enforcement")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun enforcement(@RequestBody topUpUo: TopUpUo)

    @ApiOperation(tags = ["cash"], value = "withdraw -> query")
    fun withdraw(
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("minCreatedTime") minCreatedTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("maxCreatedTime") maxCreatedTime: LocalDateTime
    ): List<WithdrawVo>

    @ApiOperation(tags = ["cash"], value = "withdraw -> check")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun withdrawCheck(@RequestBody withdrawUo: WithdrawUo)

}
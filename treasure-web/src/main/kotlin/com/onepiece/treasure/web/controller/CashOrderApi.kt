package com.onepiece.treasure.web.controller

import com.onepiece.treasure.account.model.enums.TopUpState
import com.onepiece.treasure.web.controller.value.TopUpUo
import com.onepiece.treasure.web.controller.value.TopUpVo
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

}
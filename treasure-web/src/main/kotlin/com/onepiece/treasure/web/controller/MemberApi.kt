package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.value.internet.web.MemberCoReq
import com.onepiece.treasure.beans.value.internet.web.MemberPage
import com.onepiece.treasure.beans.value.internet.web.MemberUoReq
import com.onepiece.treasure.beans.value.internet.web.WalletVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDateTime

@Api(tags = ["user"], description = " ")
interface MemberApi {

    @ApiOperation(tags = ["user"], value = "")
    fun query(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") endTime: LocalDateTime,
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam(value = "levelId", required = false) levelId: Int?,
            @RequestParam(value = "status", required = false) status: Status?,
            @RequestParam(defaultValue = "0") current: Int,
            @RequestParam(defaultValue = "10") size: Int
    ): MemberPage

    @ApiOperation(tags = ["user"], value = "")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun update(@RequestBody memberUoReq: MemberUoReq)

    @ApiOperation(tags = ["user"], value = "")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody memberCoReq: MemberCoReq)



    @ApiOperation(tags = ["user"], value = "member -> balance detail")
    fun balance(
            @PathVariable(value = "memberId") memberId: Int
    ): WalletVo




}
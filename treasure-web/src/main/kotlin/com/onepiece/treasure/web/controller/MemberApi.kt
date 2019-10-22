package com.onepiece.treasure.web.controller

import com.onepiece.treasure.account.model.enums.Status
import com.onepiece.treasure.web.controller.value.BalanceDetail
import com.onepiece.treasure.web.controller.value.MemberPage
import com.onepiece.treasure.web.controller.value.MemberUo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["user"], description = " ")
interface MemberApi {

    @ApiOperation(tags = ["user"], value = "member -> query")
    fun query(
            @RequestParam(value = "id") id: Int,
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam(value = "levelId", required = false) levelId: Int?,
            @RequestParam(value = "status", required = false) status: Status?,
            @RequestParam(defaultValue = "0") current: Int,
            @RequestParam(defaultValue = "10") size: Int
    ): MemberPage

    @ApiOperation(tags = ["user"], value = "member -> update")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun change(
            @RequestBody memberUo: MemberUo
    )

    @ApiOperation(tags = ["user"], value = "member -> balance detail")
    fun balance(@PathVariable("memberId") memberId: Int): BalanceDetail




}
package com.onepiece.treasure.su.controller

import com.onepiece.treasure.su.controller.value.UserValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["client"], description = " ")
interface UserApi {

    @ApiOperation(tags = ["client"], value = "平台 -> 开通")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun login(@RequestBody loginReq: UserValue.LoginReq): UserValue.LoginRes


}
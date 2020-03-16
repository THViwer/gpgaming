package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.su.controller.value.LoginValue
import com.onepiece.gpgaming.su.controller.value.UserValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["client"], description = " ")
interface UserApi {

    @ApiOperation(tags = ["user"], value = "登陆")
    fun login(@RequestBody loginReq: LoginValue.LoginReq): LoginValue.LoginResp

    @ApiOperation(tags = ["client"], value = "平台 -> 开通")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun login(@RequestBody loginReq: UserValue.LoginReq): UserValue.LoginRes


}
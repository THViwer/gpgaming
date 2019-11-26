package com.onepiece.treasure.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import springfox.documentation.annotations.ApiIgnore

@Api(tags = ["platform auth"], description = " ")
interface PlatformAuthApi {

    @ApiIgnore
    @ApiOperation(tags = ["platform auth"], value = "mega 登陆")
    fun login(
            @RequestParam("d") d: Int,
            @RequestBody loginReq: PlatformAuthApiController.LoginReq): PlatformAuthApiController.LoginResult


    @ApiOperation(tags = ["platform auth"], value = "下载mega的app")
    fun download(@RequestHeader("clientId", defaultValue = "1") clientId: Int): String

    @ApiOperation(tags = ["platform auth"], value = "cmd登陆")
    fun cmdLogin(
            @RequestParam("token") token: String,
            @RequestParam("secret_key") secret_key: String
    ): String


}
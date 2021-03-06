package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.games.bet.JacksonMapUtil
import com.onepiece.gpgaming.player.controller.value.PlatformAuthValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Api(tags = ["open"], description = " ")
interface OpenApi {

    @ApiOperation(tags = ["open"], value = "gameplay login")
    fun gamePlayLogin(): String

    @ApiOperation(tags = ["platform auth"], value = "mega 登陆")
    fun login(
            @PathVariable("clientId") clientId: Int
    ): String


    @ApiOperation(tags = ["platform auth"], value = "下载mega的app")
    fun download(@RequestHeader("clientId", defaultValue = "1") clientId: Int): String

    @ApiOperation(tags = ["platform auth"], value = "cmd登陆")
    fun cmdLogin(
            @RequestParam("token", required = false) token: String?,
            @RequestParam("secret_key", required = false) secret_key: String?
    ): String

    @ApiOperation(tags = ["platform auth"], value = "spadeGaming 登陆")
    fun spadeGamingLogin(@RequestBody request: PlatformAuthValue.SpadeGamingRequest): PlatformAuthValue.SpadeGamingResponse

    @ApiOperation(tags = ["platform auth"], value = "png 同步订单")
    fun pngCallData(@RequestBody jacksonMapUtil: JacksonMapUtil)


}
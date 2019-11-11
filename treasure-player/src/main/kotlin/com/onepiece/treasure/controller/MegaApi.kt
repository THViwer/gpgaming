package com.onepiece.treasure.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import springfox.documentation.annotations.ApiIgnore

@Api(tags = ["mega"], description = " ")
interface MegaApi {

    @ApiIgnore
    @ApiOperation(tags = ["mega"], value = "")
    fun login(
            @RequestParam("d") d: Int,
            @RequestBody loginReq: MegaApiController.LoginReq): MegaApiController.LoginResult


    @ApiOperation(tags = ["mega"], value = "下载mega的app")
    fun download(@RequestHeader("clientId", defaultValue = "1") clientId: Int): String


}
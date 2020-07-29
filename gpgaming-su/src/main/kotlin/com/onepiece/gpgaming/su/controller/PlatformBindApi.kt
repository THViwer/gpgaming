package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.su.controller.value.PlatformBindSuValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["client"], description = " ")
interface PlatformBindApi {

    @ApiOperation(tags = ["client"], value = "平台 -> 开通")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody platformBindCoReq: PlatformBindSuValue.PlatformBindCoReq)

    @ApiOperation(tags = ["client"], value = "平台 -> 修改")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody platformBindUoReq: PlatformBindSuValue.PlatformBindUoReq)

    @ApiOperation(tags = ["client"], value = "平台 -> 默认图标")
    fun getDefaultLogo(@RequestParam("platform") platform: Platform): PlatformBindSuValue.DefaultLogo

    @ApiOperation(tags = ["client"], value = "平台 -> 列表")
    fun clientPlatform(@PathVariable("clientId") clientId: Int): List<PlatformBindSuValue.PlatformBindVo>

}
package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.value.internet.web.PlatformValue
import com.onepiece.gpgaming.beans.value.internet.web.PlatformVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["index"], description = "首页")
interface ClientPlatformApi {

    @ApiOperation(tags = ["index"], value = "厅主平台 -> 列表")
    fun all(): List<PlatformVo>

    @ApiOperation(tags = ["index"], value = "厅主平台 -> 已开通列表")
    fun openList(): List<PlatformVo>

    @ApiOperation(tags = ["index"], value = "厅主平台 -> 更新热门、最新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody uo: PlatformValue.PlatformBindUo)

//    @ApiOperation(tags = ["setting"], value = "厅主平台 -> 更新")
//    @ResponseStatus(code = HttpStatus.NO_CONTENT)
//    fun update(@RequestBody platformUoReq: PlatformUoReq)

}
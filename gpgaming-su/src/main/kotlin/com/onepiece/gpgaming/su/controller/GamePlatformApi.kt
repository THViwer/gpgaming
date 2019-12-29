package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.beans.model.GamePlatform
import com.onepiece.gpgaming.beans.value.database.GamePlatformValue
import com.onepiece.gpgaming.su.controller.value.ClientSuValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["platform"], description = " ")
interface GamePlatformApi {

    @ApiOperation(tags = ["client"], value = "平台 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody gamePlatformCo: GamePlatformValue.GamePlatformCo)

    @ApiOperation(tags = ["client"], value = "平台 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody gamePlatformUo: GamePlatformValue.GamePlatformUo)

    @ApiOperation(tags = ["client"], value = "平台 -> 列表")
    fun list(): List<GamePlatform>


}
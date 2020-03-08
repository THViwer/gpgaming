package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.SlotGame
import com.onepiece.gpgaming.beans.value.database.SlotGameValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["slot"], description = " ")
interface SlotGameApi {

    @ApiOperation(tags = ["slot"], value = "slot -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody co: SlotGameValue.SlotGameCo)

    @ApiOperation(tags = ["slot"], value = "slot -> 修改")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody uo: SlotGameValue.SlotGameUo)

    @ApiOperation(tags = ["slot"], value = "slot -> 列表")
    fun list(@RequestParam("platform") platform: Platform): List<SlotGame>

}
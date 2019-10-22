package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.value.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["level"], description = " ")
interface LevelApi {

    @ApiOperation(tags = ["level"], value = "all")
    fun all(): List<LevelVo>

    @ApiOperation(tags = ["level"], value = "可用列表")
    fun normalList(): List<LevelVo>

    @ApiOperation(tags = ["level"], value = "create")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody levelCo: LevelCo)

    @ApiOperation(tags = ["level"], value = "update")
    fun update(@RequestBody levelUo: LevelUo)

    @ApiOperation(tags = ["level"], value = "level move")
    fun move(
            @RequestBody levelMoveDo: LevelMoveDo
    ): LevelMoveVo

    @ApiOperation(tags = ["level"], value = "check level move")
    fun checkMove(@PathVariable("sequence") sequence: String): LevelMoveCheckVo

}
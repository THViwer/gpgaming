package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.value.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["user"], description = " ")
interface LevelApi {

    @ApiOperation(tags = ["user"], value = "level -> all")
    fun all(): List<LevelVo>

    @ApiOperation(tags = ["user"], value = "level -> level list can use")
    fun normalList(): List<LevelVo>

    @ApiOperation(tags = ["user"], value = "level -> create")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody levelCo: LevelCo)

    @ApiOperation(tags = ["user"], value = "level -> update")
    fun update(@RequestBody levelUo: LevelUo)

    @ApiOperation(tags = ["user"], value = "level -> level move")
    fun move(
            @RequestBody levelMoveDo: LevelMoveDo
    ): LevelMoveVo

    @ApiOperation(tags = ["user"], value = "level -> check level move")
    fun checkMove(@PathVariable("sequence") sequence: String): LevelMoveCheckVo

}
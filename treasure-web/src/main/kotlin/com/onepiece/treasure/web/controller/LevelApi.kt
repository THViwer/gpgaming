package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.value.LevelCo
import com.onepiece.treasure.web.controller.value.LevelUo
import com.onepiece.treasure.web.controller.value.LevelVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
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

}
package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.internet.web.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["user"], description = " ")
interface LevelApi {

    @ApiOperation(tags = ["user"], value = "层级 -> 列表")
    fun all(): List<LevelVo>

    @ApiOperation(tags = ["user"], value = "层级 -> 可用列表")
    fun normalList(): List<LevelVo>

    @ApiOperation(tags = ["user"], value = "层级 -> 创建")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody levelCoReq: LevelCoReq)

    @ApiOperation(tags = ["user"], value = "层级 -> 更新")
    fun update(@RequestBody levelUoReq: LevelUoReq)

    @ApiOperation(tags = ["user"], value = "层级 -> 移动(未实现)")
    fun move(
            @RequestBody levelMoveDo: LevelMoveDo
    ): LevelMoveVo

    @ApiOperation(tags = ["user"], value = "层级 -> 检查移动是否完成(未实现)")
    fun checkMove(@PathVariable("sequence") sequence: String): LevelMoveCheckVo

}
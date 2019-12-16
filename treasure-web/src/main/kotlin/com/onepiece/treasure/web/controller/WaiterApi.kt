package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.value.internet.web.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.ResponseStatus


@Api(tags = ["user"], description = " ")
interface WaiterApi {

    @ApiOperation(tags = ["user"], value = "客服 -> 列表")
    fun query(): List<WaiterVo>

    @ApiOperation(tags = ["user"], value = "客服 -> 创建")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody waiterCoReq: WaiterCoReq)

    @ApiOperation(tags = ["user"], value = "客服 -> 更新")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun update(@RequestBody waiterUoReq: WaiterUoReq)

    @ApiOperation(tags = ["user"], value = "客服 -> 权限列表")
    fun permission(
            @RequestHeader("language") language: Language,
            @PathVariable("waiterId") waiterId: Int
    ): List<PermissionValue.PermissionVo>

    @ApiOperation(tags = ["user"], value = "客服 -> 权限修改")
    fun permission(@RequestBody req: PermissionValue.PermissionReq)


}
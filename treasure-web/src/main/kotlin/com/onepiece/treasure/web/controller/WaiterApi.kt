package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.internet.web.PermissionVo
import com.onepiece.treasure.beans.value.internet.web.WaiterCoReq
import com.onepiece.treasure.beans.value.internet.web.WaiterUoReq
import com.onepiece.treasure.beans.value.internet.web.WaiterVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus


@Api(tags = ["user"], description = " ")
interface WaiterApi {

    @ApiOperation(tags = ["user"], value = "waiter -> query")
    fun query(): List<WaiterVo>

    @ApiOperation(tags = ["user"], value = "waiter -> create")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody waiterCoReq: WaiterCoReq)

    @ApiOperation(tags = ["user"], value = "waiter -> update")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun update(@RequestBody waiterUoReq: WaiterUoReq)

    @ApiOperation(tags = ["user"], value = "waiter -> permissions")
    fun permission(@PathVariable("waiterId") waiterId: Int): List<PermissionVo>


}
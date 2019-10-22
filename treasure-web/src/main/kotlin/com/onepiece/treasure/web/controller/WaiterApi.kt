package com.onepiece.treasure.web.controller

import com.onepiece.treasure.account.model.enums.Status
import com.onepiece.treasure.web.controller.value.PermissionVo
import com.onepiece.treasure.web.controller.value.WaiterCo
import com.onepiece.treasure.web.controller.value.WaiterUo
import com.onepiece.treasure.web.controller.value.WaiterVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus


@Api(tags = ["waiter"], description = " ")
interface WaiterApi {

    @ApiOperation(tags = ["waiter"], value = "query")
    fun query(
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam(value = "name", required = false) name: String?,
            @RequestParam(value = "status", required = false) status: Status?
    ): List<WaiterVo>

    @ApiOperation(tags = ["waiter"], value = "create")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody waiterCo: WaiterCo)

    @ApiOperation(tags = ["waiter"], value = "update")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun update(@RequestBody waiterUo: WaiterUo)

    @ApiOperation(tags = ["waiter"], value = "permissions")
    fun permission(@PathVariable("memberId") memberId: Int): List<PermissionVo>


}
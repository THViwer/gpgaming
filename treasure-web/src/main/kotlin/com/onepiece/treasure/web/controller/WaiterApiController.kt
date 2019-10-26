package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.WaiterCo
import com.onepiece.treasure.beans.value.database.WaiterUo
import com.onepiece.treasure.beans.value.internet.web.PermissionVo
import com.onepiece.treasure.beans.value.internet.web.WaiterCoReq
import com.onepiece.treasure.beans.value.internet.web.WaiterUoReq
import com.onepiece.treasure.beans.value.internet.web.WaiterVo
import com.onepiece.treasure.core.service.PermissionService
import com.onepiece.treasure.core.service.WaiterService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/waiter")
class WaiterApiController(
        private val waiterService: WaiterService,
        private val permissionService: PermissionService
) : BasicController(), WaiterApi {

    @GetMapping
    override fun query(): List<WaiterVo> {
        return waiterService.findClientWaiters(clientId).map {
            with(it) {
                WaiterVo(id = id, username = username, name = name, status = status, createdTime = createdTime,
                        loginIp = loginIp, loginTime = loginTime)
            }
        }
    }

    @PostMapping
    override fun create(@RequestBody waiterCoReq: WaiterCoReq) {
        val waiterCo = WaiterCo(clientId = clientId, username = waiterCoReq.username, name = waiterCoReq.name,
                password = waiterCoReq.password)
        waiterService.create(waiterCo)
    }

    @PutMapping
    override fun update(@RequestBody waiterUoReq: WaiterUoReq) {

        val hasWaiter = waiterService.get(waiterUoReq.id)
        check(hasWaiter.clientId == clientId) { OnePieceExceptionCode.AUTHORITY_FAIL }

        val waiterUo = WaiterUo(id = waiterUoReq.id, name = waiterUoReq.name, status = waiterUoReq.status,
                password = waiterUoReq.password)
        waiterService.update(waiterUo)
    }

    @GetMapping("/permission/{waiterId}")
    override fun permission(@PathVariable("waiterId") waiterId: Int): List<PermissionVo> {

        val permission = permissionService.findWaiterPermissions(waiterId = waiterId)

        //TODO 加上其它全部的  现在暂时只是数据库有的
        return permission.permissions.map {
            with(it) {
                PermissionVo(resourceId = resourceId, effective = effective)
            }
        }
    }
}
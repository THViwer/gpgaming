package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.PermissionType
import com.onepiece.treasure.beans.enums.Permissions
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Permission
import com.onepiece.treasure.beans.model.PermissionDetail
import com.onepiece.treasure.beans.value.database.PermissionUo
import com.onepiece.treasure.beans.value.database.WaiterCo
import com.onepiece.treasure.beans.value.database.WaiterUo
import com.onepiece.treasure.beans.value.internet.web.*
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
        val clientId = getClientId()

        return waiterService.findClientWaiters(clientId).map {
            with(it) {
                WaiterVo(id = id, username = username, name = name, status = status, createdTime = createdTime,
                        loginIp = loginIp, loginTime = loginTime)
            }
        }
    }

    @PostMapping
    override fun create(@RequestBody waiterCoReq: WaiterCoReq) {
        val clientId = getClientId()

        val waiterCo = WaiterCo(clientId = clientId, username = waiterCoReq.username, name = waiterCoReq.name,
                password = waiterCoReq.password, clientBankData = waiterCoReq.clientBanks?.joinToString(","))
        waiterService.create(waiterCo)
    }

    @PutMapping
    override fun update(@RequestBody waiterUoReq: WaiterUoReq) {
        val clientId = getClientId()

        val hasWaiter = waiterService.get(waiterUoReq.id)
        check(hasWaiter.clientId == clientId) { OnePieceExceptionCode.AUTHORITY_FAIL }

        val waiterUo = WaiterUo(id = waiterUoReq.id, name = waiterUoReq.name, status = waiterUoReq.status,
                password = waiterUoReq.password, clientBankData = waiterUoReq.clientBanks?.joinToString(","))
        waiterService.update(waiterUo)
    }

    @GetMapping("/permission/{waiterId}")
    override fun permission(
            @RequestHeader("language") language: Language,
            @PathVariable("waiterId") waiterId: Int
    ): List<PermissionValue.PermissionVo> {

        val permission = permissionService.findWaiterPermissions(waiterId = waiterId)
        val permissions = permission.permissions.map { it.resourceId to it.effective }.toMap()

//        //TODO 加上其它全部的  现在暂时只是数据库有的
//        return permission.permissions.map {
//            with(it) {
//                PermissionValue.PermissionVo(resourceId = resourceId, effective = effective)
//            }
//        }
        val defaultPermissions = PermissionType.values()
        val groupPermissions = defaultPermissions.groupBy { it.parentId }

        return (groupPermissions[-1] ?: error("")).map {
            val childPermissions = groupPermissions[it.resourceId]?.map { childPermission ->
                val name = if (language == Language.CN) childPermission.cname else childPermission.ename
                PermissionValue.PermissionVo(parentId = childPermission.parentId, resourceId = childPermission.resourceId, name = name,
                        effective = permissions[it.resourceId]?: false, permissions = null)
            }

            val name = if (language == Language.CN) it.cname else it.ename
            PermissionValue.PermissionVo(parentId = it.parentId, resourceId = it.resourceId, name = name, effective = permissions[it.resourceId]?: false,
                    permissions = childPermissions)
        }
    }

    @PutMapping("/permission")
    override fun permission(@RequestBody req: PermissionValue.PermissionReq) {
        val permissions = req.permissions.map {
            PermissionDetail(resourceId = it.resourceId, effective = it.effective)
        }
        val permissionUo = PermissionUo(waiterId = req.waiterId, permissions = permissions)
        permissionService.update(permissionUo)
    }
}
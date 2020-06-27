package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.PermissionType
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.PermissionDetail
import com.onepiece.gpgaming.beans.value.database.PermissionUo
import com.onepiece.gpgaming.beans.value.database.WaiterValue
import com.onepiece.gpgaming.beans.value.internet.web.PermissionValue
import com.onepiece.gpgaming.beans.value.internet.web.WaiterCoReq
import com.onepiece.gpgaming.beans.value.internet.web.WaiterUoReq
import com.onepiece.gpgaming.beans.value.internet.web.WaiterVo
import com.onepiece.gpgaming.core.service.ClientBankService
import com.onepiece.gpgaming.core.service.PermissionService
import com.onepiece.gpgaming.core.service.WaiterService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/waiter")
class WaiterApiController(
        private val waiterService: WaiterService,
        private val permissionService: PermissionService,
        private val clientBankService: ClientBankService
) : BasicController(), WaiterApi {

    @GetMapping
    override fun query(): List<WaiterVo> {
        val clientId = getClientId()

        val clientBanks = clientBankService.findClientBank(clientId)
                .map { it.id to it }
                .toMap()

        return waiterService.findClientWaiters(clientId).map {
            with(it) {
                val clientBankVoList = it.clientBanks?.filter { bid -> clientBanks.containsKey(bid) }?.map {  bankId ->
                    val clientBank = clientBanks[bankId]!!

                    WaiterVo.ClientBankVo(bankId = bankId, clientCardNumber = clientBank.bankCardNumber, clientCardName = clientBank.name,
                            clientBank = clientBank.bank)
                }
                WaiterVo(id = id, username = username, name = name, status = status, createdTime = createdTime,
                        loginIp = loginIp, loginTime = loginTime, clientBanks = clientBankVoList)
            }
        }
    }

    @PostMapping
    override fun create(@RequestBody waiterCoReq: WaiterCoReq) {
        val bossId = getBossId()
        val clientId = getClientId()

        check(waiterCoReq.role == Role.Waiter || waiterCoReq.role == Role.Sale) { "role is error" }

        val ownCustomerScale = waiterCoReq.ownCustomerScale ?: BigDecimal.ZERO
        val systemCustomerScale = waiterCoReq.systemCustomerScale ?: BigDecimal.ZERO

        val waiterCo = WaiterValue.WaiterCo(clientId = clientId, username = waiterCoReq.username, name = waiterCoReq.name,
                password = waiterCoReq.password, clientBankData = waiterCoReq.clientBanks?.joinToString(","),
                bossId = bossId, ownCustomerScale = ownCustomerScale, systemCustomerScale = systemCustomerScale, role = waiterCoReq.role)
        waiterService.create(waiterCo)
    }

    @PutMapping
    override fun update(@RequestBody waiterUoReq: WaiterUoReq) {
        val clientId = getClientId()

        val hasWaiter = waiterService.get(waiterUoReq.id)
        check(hasWaiter.clientId == clientId) { OnePieceExceptionCode.AUTHORITY_FAIL }


        val ownCustomerScale = waiterUoReq.ownCustomerScale ?: BigDecimal.ZERO
        val systemCustomerScale = waiterUoReq.systemCustomerScale ?: BigDecimal.ZERO

        val waiterUo = WaiterValue.WaiterUo(id = waiterUoReq.id, name = waiterUoReq.name, status = waiterUoReq.status,
                password = waiterUoReq.password, clientBankData = waiterUoReq.clientBanks?.joinToString(","),
                ownCustomerScale = ownCustomerScale, systemCustomerScale = systemCustomerScale)
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

        return (groupPermissions["-1"] ?: error("")).map {
            val childPermissions = groupPermissions[it.resourceId]?.map { childPermission ->
                val name = if (language == Language.CN) childPermission.cname else childPermission.ename
                PermissionValue.PermissionVo(parentId = childPermission.parentId, resourceId = childPermission.resourceId, name = name,
                        effective = permissions[childPermission.resourceId]?: false, permissions = null)
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
package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.PermissionType
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.value.database.ClientUo
import com.onepiece.gpgaming.beans.value.database.LoginValue
import com.onepiece.gpgaming.beans.value.database.WaiterUo
import com.onepiece.gpgaming.beans.value.internet.web.ChangePwdReq
import com.onepiece.gpgaming.beans.value.internet.web.LoginReq
import com.onepiece.gpgaming.beans.value.internet.web.LoginResp
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.PermissionService
import com.onepiece.gpgaming.core.service.WaiterService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import com.onepiece.gpgaming.web.jwt.AuthService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserApiController(
        private val clientService: ClientService,
        private val waiterService: WaiterService,
        private val authService: AuthService,
        private val permissionService: PermissionService
) : BasicController(), UserApi {

    private val log = LoggerFactory.getLogger(UserApiController::class.java)

    @PostMapping
    override fun login(@RequestBody loginReq: LoginReq): LoginResp {
        val clientId = getClientId()
        val loginValue = LoginValue(clientId = clientId, username = loginReq.username, password = loginReq.password, ip = getIpAddress())

        log.info("admin login, username = ${loginReq.username}, password = ${loginReq.password}")
        return try {
            val client = clientService.login(loginValue)
            val permissions = PermissionType.values().map { it.resourceId }.plus("-1")

            val authUser = authService.login(id = client.id, role = Role.Client, username = client.username, mAuthorities = permissions)

            LoginResp(id = client.id, clientId = client.id, username = client.username, role = Role.Client,
                    token = authUser.token, permissions = permissions)

        } catch (e: Exception) {
            val waiter = waiterService.login(loginValue)

            val permissions = permissionService.findWaiterPermissions(waiterId = waiter.id).permissions.filter { it.effective }.map { it.resourceId }

            val authUser = authService.login(id = waiter.id, role = Role.Waiter, username = waiter.username, mAuthorities = permissions)

            LoginResp(id = waiter.id, clientId = waiter.clientId, username = waiter.username, role = Role.Client,
                    token = authUser.token, permissions = permissions)
        }
    }

    @PutMapping("/password")
    override fun changePassword(@RequestBody changePwdReq: ChangePwdReq) {

        val current = this.current()

        when (current.role) {
            Role.Client -> {
                val clientUo = ClientUo(id = current.id, oldPassword = changePwdReq.oldPassword, password = changePwdReq.password, name = null, logo = null)
                clientService.update(clientUo)
            }
            Role.Waiter -> {
                val waiterUo = WaiterUo(id = current.id, oldPassword = changePwdReq.oldPassword, password = changePwdReq.password, clientBankData = null)
                waiterService.update(waiterUo)
            }
            else -> check(false) { OnePieceExceptionCode.AUTHORITY_FAIL }
        }

    }
}
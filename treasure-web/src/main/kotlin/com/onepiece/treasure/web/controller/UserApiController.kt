package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Role
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.ClientUo
import com.onepiece.treasure.beans.value.database.LoginValue
import com.onepiece.treasure.beans.value.database.WaiterUo
import com.onepiece.treasure.beans.value.internet.web.ChangePwdReq
import com.onepiece.treasure.beans.value.internet.web.LoginReq
import com.onepiece.treasure.beans.value.internet.web.LoginResp
import com.onepiece.treasure.core.service.ClientService
import com.onepiece.treasure.core.service.WaiterService
import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.web.jwt.AuthService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserApiController(
        private val clientService: ClientService,
        private val waiterService: WaiterService,
        private val authService: AuthService
) : BasicController(), UserApi {

    private val log = LoggerFactory.getLogger(UserApiController::class.java)

    @PostMapping
    override fun login(@RequestBody loginReq: LoginReq): LoginResp {
        val loginValue = LoginValue(username = loginReq.username, password = loginReq.password, ip = getIpAddress())

        log.info("admin login, username = ${loginReq.username}, password = ${loginReq.password}")
        return try {
            val client = clientService.login(loginValue)


            val authUser = authService.login(id = client.id, role = Role.Client, username = client.username)
            LoginResp(id = client.id, clientId = client.id, username = client.username, role = Role.Client, token = authUser.token)

        } catch (e: Exception) {
            log.error("", e)
            val waiter = waiterService.login(loginValue)

            val authUser = authService.login(id = waiter.id, role = Role.Waiter, username = waiter.username)
            LoginResp(id = waiter.id, clientId = waiter.clientId, username = waiter.username, role = Role.Client, token = authUser.token)
        }
    }

    @PutMapping("/password")
    override fun changePassword(@RequestBody changePwdReq: ChangePwdReq) {

        val current = this.current()

        when (current.role) {
            Role.Client -> {
                val clientUo = ClientUo(id = current.id, oldPassword = changePwdReq.oldPassword, password = changePwdReq.password, name = null)
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
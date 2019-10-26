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
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import java.util.*
import kotlin.math.log

@RestController
@RequestMapping("/user")
class UserApiController(
        private val clientService: ClientService,
        private val waiterService: WaiterService
) : BasicController(), UserApi {

    @PostMapping
    override fun login(@RequestBody loginReq: LoginReq): LoginResp {
        val loginValue = LoginValue(username = loginReq.username, password = loginReq.password, ip = currentIp)

        return try {
            val client = clientService.login(loginValue)

            LoginResp(id = client.id, clientId = client.id, username = client.username, role = Role.Client, token = UUID.randomUUID().toString())

        } catch (e: Exception) {
            e.printStackTrace()
            val waiter = waiterService.login(loginValue)

            LoginResp(id = waiter.id, clientId = waiter.clientId, username = waiter.username, role = Role.Client, token = UUID.randomUUID().toString())
        }
    }

    @PutMapping("/password")
    override fun changePassword(@RequestBody changePwdReq: ChangePwdReq) {

        when (role) {
            Role.Client -> {
                val clientUo = ClientUo(id = id, oldPassword = changePwdReq.oldPassword, password = changePwdReq.password)
                clientService.update(clientUo)
            }
            Role.Waiter -> {
                val waiterUo = WaiterUo(id = id, oldPassword = changePwdReq.oldPassword, password = changePwdReq.password)
                waiterService.update(waiterUo)
            }
            else -> check(false) { OnePieceExceptionCode.AUTHORITY_FAIL }
        }

    }
}
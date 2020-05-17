package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.value.database.LoginValue
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.player.controller.basic.BasicController
import com.onepiece.gpgaming.player.controller.value.LoginReq
import com.onepiece.gpgaming.player.controller.value.LoginResp
import com.onepiece.gpgaming.player.jwt.AuthService
import com.onepiece.gpgaming.utils.RequestUtil
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/agent")
class AgentApiController(
        private val memberService: MemberService,
        private val clientService: ClientService,
        private val authService: AuthService
) : BasicController(), AgentApi {

    @PostMapping("/login")
    override fun login(@RequestBody loginReq: LoginReq): LoginResp {
        val bossId = getBossIdByDomain()

        val loginValue = LoginValue(bossId = bossId, username = loginReq.username, password = loginReq.password, ip = RequestUtil.getIpAddress())
        val member = memberService.login(loginValue)

        check(member.role == Role.Agent) { OnePieceExceptionCode.LOGIN_FAIL }

        val token = authService.login(clientId = member.clientId, username = loginReq.username, role = member.role)
        return LoginResp(id = member.id, role = Role.Agent, username = member.username, token = token, name = member.name, autoTransfer = member.autoTransfer,
                domain = "", country = Country.Default, successful = true)

    }
}
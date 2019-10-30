package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.Role
import com.onepiece.treasure.beans.value.database.LoginValue
import com.onepiece.treasure.beans.value.database.MemberCo
import com.onepiece.treasure.beans.value.database.MemberUo
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.*
import com.onepiece.treasure.core.service.LevelService
import com.onepiece.treasure.core.service.MemberService
import com.onepiece.treasure.jwt.AuthService
import com.onepiece.treasure.jwt.JwtUser
import com.onepiece.treasure.jwt.JwtUserDetailsServiceImpl
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/user")
class UserApiController(
        private val memberService: MemberService,
        private val authService: AuthService,
        private val levelService: LevelService
) : BasicController(), UserApi {

    @PostMapping
    override fun login(@RequestBody loginReq: LoginReq): LoginResp {
        //TODO 判断域名来选择厅主

        val loginValue = LoginValue(username = loginReq.username, password = loginReq.password, ip = ip)
        val member = memberService.login(loginValue)

        val token = authService.login(loginReq.username)
        return LoginResp(id = member.id, role = Role.Member, username = member.username, token = token)

    }

    @PutMapping
    override fun register(@RequestBody registerReq: RegisterReq): LoginResp {

        val defaultLevel = levelService.getDefaultLevel(clientId = 1)

        val memberCo = MemberCo(clientId = 1, username = registerReq.username, password = registerReq.password, safetyPassword = registerReq.safetyPassword,
                levelId = defaultLevel.id)
        memberService.create(memberCo)

        val loginReq = LoginReq(username = registerReq.username, password = registerReq.password)
        return this.login(loginReq)
    }

    @GetMapping("/current")
    fun currentUser(): JwtUser {
        return current()
    }

    @PutMapping("/password")
    override fun changePassword(@RequestBody changePwdReq: ChangePwdReq) {
        val memberId = current().id
        val memberUo = MemberUo(id = memberId, oldPassword = changePwdReq.oldPassword, password = changePwdReq.password)
        memberService.update(memberUo)
    }
}
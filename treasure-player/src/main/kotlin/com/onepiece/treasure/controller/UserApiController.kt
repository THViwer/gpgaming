package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.Role
import com.onepiece.treasure.beans.value.database.LoginValue
import com.onepiece.treasure.beans.value.database.MemberUo
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.ChangePwdReq
import com.onepiece.treasure.controller.value.LoginReq
import com.onepiece.treasure.controller.value.LoginResp
import com.onepiece.treasure.controller.value.UserValueFactory
import com.onepiece.treasure.core.service.MemberService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/user")
class UserApiController(
        private val memberService: MemberService
) : BasicController(), UserApi {

    @PostMapping
    override fun login(@RequestBody loginReq: LoginReq): LoginResp {
        //TODO 判断域名来选择厅主

        val loginValue = LoginValue(username = loginReq.username, password = loginReq.password, ip = ip)
        val member = memberService.login(loginValue)

        return LoginResp(id = member.id, role = Role.Member, username = member.username, token = UUID.randomUUID().toString())

    }

    @PutMapping("/password")
    override fun changePassword(@RequestBody changePwdReq: ChangePwdReq) {
        val memberUo = MemberUo(id = id, oldPassword = changePwdReq.oldPassword, password = changePwdReq.password)
        memberService.update(memberUo)
    }
}
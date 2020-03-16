package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.su.controller.value.LoginValue
import com.onepiece.gpgaming.su.controller.value.UserValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/user")
class UserApiController : UserApi {

    @PostMapping("/login")
    override fun login(@RequestBody loginReq: LoginValue.LoginReq): LoginValue.LoginResp {

        check(loginReq.username == "su")
        check(loginReq.password == "GPGaming")

        return LoginValue.LoginResp(id = 1, username = "su", token = UUID.randomUUID().toString())

    }

    @PostMapping("/login")
    override fun login(@RequestBody loginReq: UserValue.LoginReq): UserValue.LoginRes {
        check(loginReq.username == "su" && loginReq.password == "123456") { "用户名或密码错误" }
        return UserValue.LoginRes(username = loginReq.username, token = UUID.randomUUID().toString())
    }
}
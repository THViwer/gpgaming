package com.onepiece.treasure.su.controller

import com.onepiece.treasure.su.controller.value.UserValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/user")
class UserApiController : UserApi {


    @PostMapping("/login")
    override fun login(@RequestBody loginReq: UserValue.LoginReq): UserValue.LoginRes {
        check(loginReq.username == "su" && loginReq.password == "123456") { "用户名或密码错误" }
        return UserValue.LoginRes(username = loginReq.username, token = UUID.randomUUID().toString())
    }
}
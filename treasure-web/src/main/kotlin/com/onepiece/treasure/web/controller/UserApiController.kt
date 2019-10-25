package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.beans.value.internet.web.ChangePwdReq
import com.onepiece.treasure.beans.value.internet.web.LoginReq
import com.onepiece.treasure.beans.value.internet.web.LoginResp
import com.onepiece.treasure.beans.value.internet.web.UserValueFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserApiController : BasicController(), UserApi {

    @PostMapping
    override fun login(@RequestBody loginReq: LoginReq): LoginResp {
        return UserValueFactory.generatorLoginResp()
    }

    @PutMapping("/password")
    override fun changePassword(@RequestBody changePwdReq: ChangePwdReq) {
    }
}
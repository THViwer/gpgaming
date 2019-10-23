package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.web.controller.value.ChangePwdReq
import com.onepiece.treasure.web.controller.value.LoginReq
import com.onepiece.treasure.web.controller.value.LoginResp
import com.onepiece.treasure.web.controller.value.UserValueFactory
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
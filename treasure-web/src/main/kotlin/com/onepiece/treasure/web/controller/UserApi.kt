package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.value.ChangePwdReq
import com.onepiece.treasure.web.controller.value.LoginReq
import com.onepiece.treasure.web.controller.value.LoginResp
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestBody

@Api(tags = ["user"], description = " ")
interface UserApi {

    @ApiOperation(tags = ["user"], value = "login")
    fun login(@RequestBody loginReq: LoginReq): LoginResp

    @ApiOperation(tags = ["user"], value = "change current password")
    fun changePassword(@RequestBody changePwdReq: ChangePwdReq)

}
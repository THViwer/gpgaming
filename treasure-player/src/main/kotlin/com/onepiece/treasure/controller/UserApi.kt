package com.onepiece.treasure.controller

import com.onepiece.treasure.controller.value.ChangePwdReq
import com.onepiece.treasure.controller.value.LoginReq
import com.onepiece.treasure.controller.value.LoginResp
import com.onepiece.treasure.controller.value.RegisterReq
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestBody

@Api(tags = ["user"], description = " ")
interface UserApi {

    @ApiOperation(tags = ["user"], value = "登陆")
    fun login(@RequestBody loginReq: LoginReq): LoginResp

    @ApiOperation(tags = ["user"], value = "注册")
    fun register(@RequestBody registerReq: RegisterReq): LoginResp

    @ApiOperation(tags = ["user"], value = "修改资料")
    fun changePassword(@RequestBody changePwdReq: ChangePwdReq)

}
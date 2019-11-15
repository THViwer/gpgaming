package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.internet.web.ChangePwdReq
import com.onepiece.treasure.beans.value.internet.web.LoginReq
import com.onepiece.treasure.beans.value.internet.web.LoginResp
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestBody

@Api(tags = ["login"], description = "登陆")
interface UserApi {

    @ApiOperation(tags = ["login"], value = "登陆")
    fun login(@RequestBody loginReq: LoginReq): LoginResp

    @ApiOperation(tags = ["login"], value = "修改当前密码")
    fun changePassword(@RequestBody changePwdReq: ChangePwdReq)

}
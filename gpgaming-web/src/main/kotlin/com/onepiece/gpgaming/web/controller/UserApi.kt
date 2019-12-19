package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.value.internet.web.ChangePwdReq
import com.onepiece.gpgaming.beans.value.internet.web.LoginReq
import com.onepiece.gpgaming.beans.value.internet.web.LoginResp
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["login"], description = "登陆")
interface UserApi {

    @ApiOperation(tags = ["login"], value = "登陆")
    fun login(@RequestBody loginReq: LoginReq): LoginResp

    @ApiOperation(tags = ["login"], value = "修改当前密码")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changePassword(@RequestBody changePwdReq: ChangePwdReq)

}
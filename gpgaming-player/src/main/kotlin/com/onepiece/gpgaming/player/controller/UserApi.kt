package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.player.controller.value.ChangePwdReq
import com.onepiece.gpgaming.player.controller.value.CheckUsernameResp
import com.onepiece.gpgaming.player.controller.value.LoginReq
import com.onepiece.gpgaming.player.controller.value.LoginResp
import com.onepiece.gpgaming.player.controller.value.PlatformMemberUo
import com.onepiece.gpgaming.player.controller.value.PlatformMemberVo
import com.onepiece.gpgaming.player.controller.value.RegisterReq
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["user"], description = " ")
interface UserApi {

    @ApiOperation(tags = ["user"], value = "登陆")
    fun login(@RequestBody loginReq: LoginReq): LoginResp

    @ApiOperation(tags = ["user"], value = "更新自动转账配置")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun upAutoTransfer(@RequestParam("autoTransfer") autoTransfer: Boolean)

    @ApiOperation(tags = ["user"], value = "注册")
    fun register(@RequestBody registerReq: RegisterReq): LoginResp

    @ApiOperation(tags = ["user"], value = "检查用户名是否存在")
    fun checkUsername(@PathVariable("username") username: String): CheckUsernameResp

    @ApiOperation(tags = ["user"], value = "检查手机号是否已存在")
    fun checkPhone(@PathVariable("phone") phone: String): CheckUsernameResp

    @ApiOperation(tags = ["user"], value = "修改资料")
    fun changePassword(@RequestBody changePwdReq: ChangePwdReq)

    @ApiOperation(tags = ["user"], value = "平台用户列表")
    fun platformUsers(): List<PlatformMemberVo>

    @ApiOperation(tags = ["user"], value = "平台用户 -> 修改")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun platformUser(@RequestBody platformMemberUo: PlatformMemberUo)

}

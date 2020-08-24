package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.player.controller.value.ChangePwdReq
import com.onepiece.gpgaming.player.controller.value.CheckUsernameResp
import com.onepiece.gpgaming.player.controller.value.LoginByAdminReq
import com.onepiece.gpgaming.player.controller.value.LoginByAdminResponse
import com.onepiece.gpgaming.player.controller.value.LoginReq
import com.onepiece.gpgaming.player.controller.value.LoginResp
import com.onepiece.gpgaming.player.controller.value.PlatformMemberUo
import com.onepiece.gpgaming.player.controller.value.PlatformMemberVo
import com.onepiece.gpgaming.player.controller.value.RegisterReq
import com.onepiece.gpgaming.player.controller.value.UserValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["user"], description = " ")
interface UserApi {

    @ApiOperation(tags = ["user"], value = "登陆")
    fun login(@RequestBody loginReq: LoginReq): LoginResp

    @ApiOperation(tags = ["user"], value = "登陆(admin)")
    fun login(@RequestBody req: LoginByAdminReq): LoginByAdminResponse

    @ApiOperation(tags = ["user"], value = "查看登陆信息")
    fun loginDetail(): LoginResp

    @ApiOperation(tags = ["user"], value = "更新自动转账配置")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun upAutoTransfer(@RequestParam("autoTransfer") autoTransfer: Boolean)

    @ApiOperation(tags = ["user"], value = "注册")
    fun register(@RequestBody registerReq: RegisterReq): LoginResp

    @ApiOperation(tags = ["user"], value = "添加营销点击量")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun addMarketView(@RequestParam("marketId") marketId: Int)

    @ApiOperation(tags = ["user"], value = "可选国家列表")
    fun countries(): List<Country>

    @ApiOperation(tags = ["user"], value = "检查用户名是否存在")
    fun checkUsername(@PathVariable("username") username: String): CheckUsernameResp

    @ApiOperation(tags = ["user"], value = "检查手机号是否已存在")
    fun checkPhone(@PathVariable("phone") phone: String): CheckUsernameResp

    @ApiOperation(tags = ["user"], value = "修改密码")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun changePassword(@RequestBody changePwdReq: ChangePwdReq)

    @ApiOperation(tags = ["user"], value = "修改基础资料")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun changeUserInfo(@RequestBody uo: UserValue.UserInfoUo)

    @ApiOperation(tags = ["user"], value = "平台用户列表")
    fun platformUsers(): List<PlatformMemberVo>

    @ApiOperation(tags = ["user"], value = "平台用户 -> 修改")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun platformUser(@RequestBody platformMemberUo: PlatformMemberUo)

    @ApiOperation(tags = ["user"], value = "平台用户 -> 当前推广信息")
    fun myIntroduceDetail(): UserValue.MyIntroduceDetail

    @ApiOperation(tags = ["user"], value = "平台用户 -> 当前推广列表")
    fun myIntroduceList(): List<UserValue.MyIntroduceVo>

}

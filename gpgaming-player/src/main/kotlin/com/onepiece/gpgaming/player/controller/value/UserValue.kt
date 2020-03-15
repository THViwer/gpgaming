package com.onepiece.gpgaming.player.controller.value

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Role
import io.swagger.annotations.ApiModelProperty
import java.util.*

data class LoginResp(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("真实姓名")
        val name: String,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("角色")
        val role: Role,

        @ApiModelProperty("token")
        val token: String
)

data class CheckUsernameResp(
        @ApiModelProperty("是否存在")
        val exist: Boolean
)

data class LoginReq(

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("密码")
        val password: String
)
data class RegisterReq(

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("密码")
        val password: String,

        @ApiModelProperty("用户真实姓名")
        val name: String = "张三",

        @ApiModelProperty("手机号")
        val phone: String,

        @ApiModelProperty("安全密码")
        val safetyPassword: String = "1234",

        @ApiModelProperty("推广码")
        val promoteSource: String?
)

data class ChangePwdReq(

        @ApiModelProperty("旧密码")
        val oldPassword: String,

        @ApiModelProperty("新密码")
        val password: String

)

data class PlatformMemberVo(
        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("平台")
        val platform: Platform,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("密码")
        val password: String,

        @ApiModelProperty("排序")
        val sort: Int
) {

        val pname: String
                @ApiModelProperty("平台名称")
                get() {
                        return platform.pname
                }
}

data class PlatformMemberUo(

        @ApiModelProperty("平台")
        val platform: Platform,

        @ApiModelProperty("密码")
        val password: String
)


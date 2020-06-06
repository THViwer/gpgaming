package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Role
import io.swagger.annotations.ApiModelProperty

object UserValueFactory {

//    fun generatorLoginResp(): LoginResp {
//        return LoginResp(id = 1, username = "zhangsan", role = Role.Client, token = UUID.randomUUID().toString())
//    }

}

data class LoginResp(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("厅主Id")
        val clientId: Int,

        @ApiModelProperty("是否是主业主")
        val main: Boolean,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("角色")
        val role: Role,

        @ApiModelProperty("token")
        val token: String,

        @ApiModelProperty("权限")
        val permissions: List<String>
)

data class LoginReq(

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("密码")
        val password: String
)

data class ChangePwdReq(

        @ApiModelProperty("旧密码")
        val oldPassword: String,

        @ApiModelProperty("新密码")
        val password: String

)
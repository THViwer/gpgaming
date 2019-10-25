package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.Role
import io.swagger.annotations.ApiModelProperty
import java.util.*

object UserValueFactory {

    fun generatorLoginResp(): LoginResp {
        return LoginResp(id = 1, username = "zhangsan", role = Role.Client, token = UUID.randomUUID().toString())
    }

}

data class LoginResp(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("角色")
        val role: Role,

        @ApiModelProperty("token")
        val token: String
)

data class LoginReq(

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("密码")
        val passwd: String
)

data class ChangePwdReq(

        @ApiModelProperty("旧密码")
        val passwd: String,

        @ApiModelProperty("新密码")
        val newPasswd: String

)
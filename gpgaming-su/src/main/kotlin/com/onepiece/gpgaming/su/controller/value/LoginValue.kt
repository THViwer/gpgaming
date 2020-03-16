package com.onepiece.gpgaming.su.controller.value

import com.onepiece.gpgaming.beans.enums.Role
import io.swagger.annotations.ApiModelProperty

sealed class LoginValue {

    data class LoginReq(

            @ApiModelProperty("用户名")
            val username: String,

            @ApiModelProperty("密码")
            val password: String
    )

    data class LoginResp(

            @ApiModelProperty("id")
            val id: Int,

            @ApiModelProperty("用户名")
            val username: String,

            @ApiModelProperty("token")
            val token: String
    )

}
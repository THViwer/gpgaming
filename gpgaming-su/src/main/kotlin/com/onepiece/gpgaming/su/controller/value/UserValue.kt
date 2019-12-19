package com.onepiece.gpgaming.su.controller.value

sealed class UserValue {

    data class LoginReq(
            val username: String,

            val password: String
    )

    data class LoginRes(
            val username: String,

            val token: String
    )

}
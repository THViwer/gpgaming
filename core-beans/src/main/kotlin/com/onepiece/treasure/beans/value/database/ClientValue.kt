package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

data class ClientCo(

        // 品牌
        val brand: String,

        // 用户名
        val username: String,

        // 用户密码
        val password: String,

        // 登陆时间
        val loginTime: LocalDateTime
)

data class ClientUo(

        // 用户Id
        val id: Int,

        // 旧密码
        val oldPassword: String? = null,

        // 用户密码
        val password: String? = null,

        // 厅主状态
        val status: Status? = null,

        // 请求Ip
        val ip: String? = null,

        // 登陆时间
        val loginTime: LocalDateTime? = null
)
package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Role
import java.time.LocalDateTime

data class LoginHistory(

        // id
        val id: Int,

        // bossId
        val bossId: Int,

        // 业主id
        val clientId: Int,

        // 用户Id
        val userId: Int,

        val username: String,

        // 角色
        val role: Role,

        // ip
        val ip: String,

        // 国家
        val country: String,

        // 创建时间
        val createdTime: LocalDateTime

)
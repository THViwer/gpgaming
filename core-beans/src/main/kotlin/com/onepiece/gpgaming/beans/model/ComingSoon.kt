package com.onepiece.gpgaming.beans.model

import java.time.LocalDateTime

data class ComingSoon (

        // id
        val id: Int,

        // ip
        val ip: String,

        // 邮箱
        val email: String,

        // 创建时间
        val createdTime: LocalDateTime

)
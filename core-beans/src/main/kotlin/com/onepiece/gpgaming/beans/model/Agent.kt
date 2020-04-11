package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

data class Agent(

        // id
        val id: Int,

        // 用户Id
        val clientId: Int,

        // 用户名
        val username: String,

        // 密码
        val password: String,

        // 佣金比例
        val proportion: BigDecimal,

        // 状态
        val status: Status,

        // 推广code
        val code: String,

        // 创建时间
        val createdTime: LocalDateTime
)
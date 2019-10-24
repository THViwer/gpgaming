package com.onepiece.treasure.core.model

import com.onepiece.treasure.core.model.enums.Status
import java.time.LocalDateTime

/**
 * 会员信息表
 */
data class csMember(
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 用户名
        val username: String,

        // 密码
        val password: String,

        // 等级Id
        val levelId: Int,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime,

        // 登陆时间
        val loginTime: LocalDateTime
)
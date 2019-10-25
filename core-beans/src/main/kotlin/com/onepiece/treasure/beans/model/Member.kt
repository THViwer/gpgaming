package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

/**
 * 会员信息表
 */
data class Member(
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

        // 登陆Ip
        val loginIp: String?,

        // 登陆时间
        val loginTime: LocalDateTime?
)
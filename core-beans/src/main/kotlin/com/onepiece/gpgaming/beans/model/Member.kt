package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Status
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

        // 姓名
        val name: String,

        // 手机号
        val phone: String,

        // 密码
        val password: String,

        // 安全密码
        val safetyPassword: String,

        // 等级Id
        val levelId: Int,

        // 是否已有首充值
        val firstPromotion: Boolean,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime,

        // 登陆Ip
        val loginIp: String?,

        // 推广来源
        val promoteSource: String?,

        // 登陆时间
        val loginTime: LocalDateTime?
)
package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

/**
 * 会员信息表
 */
data class Member(

        // id
        val id: Int,

        // bossId
        val bossId: Int,

        // 厅主Id
        val clientId: Int,

        // 代理Id
        val agentId: Int,

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

        // 启动游戏自动转账
        val autoTransfer: Boolean,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime,

        // 代理
        val role: Role,

        // 登陆Ip
        val loginIp: String?,

        // 推广码
        val promoteCode: String,

        // 登陆时间
        val loginTime: LocalDateTime?
)
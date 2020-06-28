package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.SaleScope
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
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

        // 销售员Id 当role = Member时才有
        val saleId: Int,

        // 电销类型
        val saleScope: SaleScope,

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

        //TODO 下面 是代理独有的字段

        // 推广码
        val promoteCode: String,

        // 是否是正式
        val formal: Boolean,

        // 每月的代理费
        val agencyMonthFee: BigDecimal,

        // 登陆时间
        val loginTime: LocalDateTime?
)
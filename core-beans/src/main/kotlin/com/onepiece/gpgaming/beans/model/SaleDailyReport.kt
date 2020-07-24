package com.onepiece.gpgaming.beans.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 电销日报表
 */
data class SaleDailyReport (

        val id: Int,

        // bossId
        val bossId: Int,

        // clientId
        val clientId: Int,

        // 电销Id
        val saleId: Int,

        // 日期
        val day: LocalDate,

        // 电销用户名
        val saleUsername: String,

        // 总充值
        val ownTotalDeposit: BigDecimal,

        // 总取款
        val ownTotalWithdraw: BigDecimal,

        // 总优惠
        val ownTotalPromotion: BigDecimal,

        // 总返水
        val ownTotalRebate: BigDecimal,

        // 自己的顾客佣金比例
        val ownCustomerScale: BigDecimal,

        // 自己的顾客佣金
        val ownCustomerFee: BigDecimal,

        // 自己新增会员总数
        val ownMemberCount: Int,

        // 总充值
        val systemTotalDeposit: BigDecimal,

        // 总取款
        val systemTotalWithdraw: BigDecimal,

        // 总优惠
        val systemTotalPromotion: BigDecimal,

        // 总返水
        val systemTotalRebate: BigDecimal,

        // 系统佣金比例
        val systemCustomerScale: BigDecimal,

        // 系统的顾客佣金
        val systemCustomerFee: BigDecimal,

        // 系统新增会员总数
        val systemMemberCount: Int,

        // 创建时间
        val createdTime: LocalDateTime

)
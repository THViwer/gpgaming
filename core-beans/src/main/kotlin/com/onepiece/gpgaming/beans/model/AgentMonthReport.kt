package com.onepiece.gpgaming.beans.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime


data class AgentMonthReport(

        // id
        val id: Int,

        // 日期
        val day: LocalDate,

        // bossId
        val bossId: Int,

        // main业主Id
        val clientId: Int,

        // 上级代理Id
        val superiorAgentId: Int,

        // 代理Id
        val agentId: Int,

        // 代理佣金
        val agentCommission: BigDecimal,

        // 代理活跃人数
        val agentActiveCount: Int,

        // 代理佣金比例
        val agentCommissionScale: BigDecimal,

        // 会员佣金
        val memberCommission: BigDecimal,

        // 会员活跃人数
        val memberActiveCount: Int,

        // 会员佣金比例
        val memberCommissionScale: BigDecimal,

        // 会员充值
        val totalDeposit: BigDecimal,

        // 会员取款
        val totalWithdraw: BigDecimal,

        // 总返水金额
        val totalRebate: BigDecimal,

        // 总优惠金额
        val totalPromotion: BigDecimal,

        // 当前总下注
        val totalBet: BigDecimal,

        // 当前顾客盈利
        val totalMWin: BigDecimal,

        // 佣金是否已执行
        val commissionExecution: Boolean,

        // 新增会员数
        val newMemberCount: Int,

        // 代理月费
        val agencyMonthFee: BigDecimal,

        // 创建时间
        val createdTime: LocalDateTime

) {

    // 总佣金
    val totalCommission: BigDecimal = agentCommission.plus(memberCommission).minus(agencyMonthFee)


}
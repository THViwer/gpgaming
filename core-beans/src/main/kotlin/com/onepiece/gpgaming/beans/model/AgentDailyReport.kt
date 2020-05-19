package com.onepiece.gpgaming.beans.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class AgentDailyReport(

        // id
        val id: Int,

        // bossId
        val bossId: Int,

        // main业主Id
        val clientId:  Int,

        // 上级代理Id
        val superiorAgentId: Int,

        // 代理Id
        val agentId: Int,

        // 会员充值
        val totalDeposit: BigDecimal,

        // 会员取款
        val totalWithdraw: BigDecimal,

        // 当前总下注
        val totalBet: BigDecimal,

        // 当前顾客盈利
        val totalMWin: BigDecimal,

        // 总返水
        val totalRebate: BigDecimal,

        // 总优惠金额
        val totalPromotion: BigDecimal,

        // 新增会员数
        val newMemberCount: Int,

        // 日期
        val day: LocalDate,

        // 创建时间
        val createdTime: LocalDateTime

)

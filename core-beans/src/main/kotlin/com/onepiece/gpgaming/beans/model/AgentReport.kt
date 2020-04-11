package com.onepiece.gpgaming.beans.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class AgentReport (

        // id
        val id: Int,

        // 业主Id
        val clientId: Int,

        // 代理Id
        val agentId: Int,

        // 顾客盈利
        val totalMWin: BigDecimal,

        // 顾客下注
        val totalBet: BigDecimal,

        // 存款次数
        val depositCount: Int,

        // 充值金额
        val depositMoney: BigDecimal,

        // 取款次数
        val withdrawCount: Int,

        // 人工提存金额
        val artificialMoney: BigDecimal,

        // 人工提存次数
        val artificialCount: Int,

        // 自动入款金额
        val thirdPayMoney: BigDecimal,

        // 自动入款次数
        val thirdPayCount: Int,

        // 取款金额
        val withdrawMoney: BigDecimal,

        // 返水比例
        val backwater: BigDecimal,

        // 近水金额
        val backwaterMoney: BigDecimal,

        // 佣金比例
        val proportion: BigDecimal,

        // 佣金
        val commission: BigDecimal,

        // 创建时间
        val createdTime: LocalDateTime
)
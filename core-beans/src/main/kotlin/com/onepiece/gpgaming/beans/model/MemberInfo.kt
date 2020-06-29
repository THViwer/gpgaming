package com.onepiece.gpgaming.beans.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class MemberInfo(

        // 基础信息
        val bossId: Int,

        val clientId: Int,

        val agentId: Int,

        val saleId: Int,

        val memberId: Int,

        val username: String,

        // 充值信息
        val totalDeposit: BigDecimal,

        val lastDepositTime: LocalDateTime?,

        val totalDepositCount: Int,


        // 取款信息
        val totalWithdraw: BigDecimal,

        val lastWithdrawTime: LocalDateTime?,

        val totalWithdrawCount: Int,

        // 登陆信息

        val registerTime: LocalDateTime,

        val lastLoginTime: LocalDateTime?,

        val loginCount: Int,

        // 电销信息
        val lastSaleTime: LocalDateTime?,

        val saleCount: Int

)
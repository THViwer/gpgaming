package com.onepiece.treasure.beans.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class ClientDailyReport(
        // id
        val id: Int,

        // 报表时间
        val day: LocalDate,

        // 厅主Id
        val clientId: Int,

//        // 下注金额
//        val bet: BigDecimal,
//
//        // 盈利金额
//        val win: BigDecimal,

        // 转入金额
        val transferIn: BigDecimal,

        // 转出金额
        val transferOut: BigDecimal,

        // 充值金额
        val depositMoney: BigDecimal,

        // 优惠金额
        val promotionAmount: BigDecimal,

        // 充值次数
        val depositCount: Int,

        // 取款金额
        val withdrawMoney: BigDecimal,

        // 取款次数
        val withdrawCount: Int,

        // 今日新增用户
        val newMemberCount: Int,

        // 创建时间
        val createdTime: LocalDateTime
)
package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class MemberReport(

        val id: Int,

        // 日期
        val day: LocalDate,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 平台Id
        val platform: Platform,

        // 下注金额
        val bet: BigDecimal,

        // 金额  正数: 会员赢钱、厅主输钱 负数：会员输钱、厅主赢钱
        val money: BigDecimal,

        // 创建时间
        val createdTime: LocalDateTime

)
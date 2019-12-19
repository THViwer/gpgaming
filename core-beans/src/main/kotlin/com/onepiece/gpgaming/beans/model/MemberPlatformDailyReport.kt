package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class MemberPlatformDailyReport(

        val id: Int,

        // 日期
        val day: LocalDate,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 平台Id
        val platform: Platform,

        // 转入金额
        val transferIn: BigDecimal,

        // 转出金额
        val transferOut: BigDecimal,

//        // 下注金额
//        val bet: BigDecimal,
//
//        // 金额  正数: 会员赢钱、厅主输钱 负数：会员输钱、厅主赢钱
//        val win: BigDecimal,

        // 创建时间
        val createdTime: LocalDateTime

)
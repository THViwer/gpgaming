package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class ClientPlatformDailyReport(

        // id
        val id: Int,

        // 报表日期
        val day: LocalDate,

        // 厅主Id
        val clientId: Int,

        // 平台
        val platform: Platform,

        // 下注金额
        val bet: BigDecimal,

        // 盈利金额
        val win: BigDecimal,

        // 转入金额
        val transferIn: BigDecimal,

        // 转出金额
        val transferOut: BigDecimal,


        // 创建时间
        val createdTime: LocalDateTime


)
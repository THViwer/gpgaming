package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class PromotionPlatformDailyReport (

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 日期
        val day: LocalDate,

        // 平台
        val platform: Platform,

        // 优惠Id
        val promotionId: Int,

        // 优惠金额
        val promotionAmount: BigDecimal,

        // 创建时间
        val createdTime: LocalDateTime

)
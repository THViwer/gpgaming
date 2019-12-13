package com.onepiece.treasure.beans.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class PromotionDailyReport (

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 日期
        val day: LocalDate,

        // 优惠Id
        val promotionId: Int,

        // 优惠金额
        val promotionAmount: BigDecimal,

        // 创建时间
        val createdTime: LocalDateTime

)
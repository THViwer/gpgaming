package com.onepiece.treasure.core.order

import java.math.BigDecimal
import java.time.LocalDateTime

data class JokerBetOrder(

        val oCode: String,

        val username: String,

        val gameCode: String,

        val description: String,

        val type: String,

        val amount: BigDecimal,

        val result: BigDecimal,

        val time: LocalDateTime,

        val appId: String,

        // 创建时间
        val createdTime: LocalDateTime
)



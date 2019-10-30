package com.onepiece.treasure.core.order

import java.math.BigDecimal
import java.time.LocalDateTime

data class JokerBetOrder(

        val oCode: String,

        val clientId: Int,

        val memberId: Int,

        val username: String,

        val gameCode: String,

        val description: String,

        val roundId: String,

        val amount: BigDecimal,

        val freeAmount: BigDecimal,

        val result: BigDecimal,

        val time: LocalDateTime,

        val details: String?,

        val appId: String,

        val currencyCode: String,

        val type: String,

        // 创建时间
        val createdTime: LocalDateTime
)



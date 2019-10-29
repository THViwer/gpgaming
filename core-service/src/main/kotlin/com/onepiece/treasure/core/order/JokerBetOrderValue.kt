package com.onepiece.treasure.core.order

import java.math.BigDecimal
import java.time.LocalDateTime

sealed class JokerBetOrderValue {

    data class Query(

            val startTime: LocalDateTime,

            val endTime: LocalDateTime,

            val username: String

    )

    data class JokerReport(

            val clientId: Int,

            val memberId: Int,

            val amount: BigDecimal,

            val result: BigDecimal

    )

}
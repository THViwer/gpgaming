package com.onepiece.treasure.core.order

import java.time.LocalDateTime

sealed class JokerBetOrderValue {

    data class Query(

            val startTime: LocalDateTime,

            val endTime: LocalDateTime,

            val username: String

    )

}
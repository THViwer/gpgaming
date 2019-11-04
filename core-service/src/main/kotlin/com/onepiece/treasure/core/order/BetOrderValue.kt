package com.onepiece.treasure.core.order

import java.math.BigDecimal
import java.time.LocalDateTime

sealed class BetOrderValue {

    data class Query(

            val startTime: LocalDateTime,

            val endTime: LocalDateTime,

            val username: String

    )

    data class Report(

            //  厅主Id
            val clientId: Int,

            // 会员 Id
            val memberId: Int,

            // 下注金额
            val bet: BigDecimal,

            // 盈利金额
            val win: BigDecimal

    )

}
package com.onepiece.gpgaming.beans.value.database

import java.math.BigDecimal
import java.time.LocalDate

sealed class PromotionDailyReportValue {

    data class Query(
            val clientId: Int,

            val startDate: LocalDate,

            val endDate: LocalDate
    )

    data class PlatformQuery(
            val clientId: Int,

            val promotionId: Int,

            val startDate: LocalDate,

            val endDate: LocalDate

    )

    data class StatisticalVo(

            val clientId: Int,

            val promotionId: Int,

            val promotionAmount: BigDecimal
    )



}
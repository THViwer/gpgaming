package com.onepiece.gpgaming.beans.value.database

import java.math.BigDecimal
import java.time.LocalDate

sealed class MarketingDailyReportValue {

    data class MarketingDailyReportQuery(

            val clientId: Int,

            val startDate: LocalDate,

            val endDate: LocalDate
    )

    data class MarketingDailyReportCo(
            // 日期
            val day: LocalDate,

            // 营销Id
            val marketingId: Int,

            // 注册量
            val registerCount: Int,

            // 访问量
            val viewCount: Int,

            // 充值金额
            val depositAmount: BigDecimal,

            // 取款金额
            val withdrawAmount: BigDecimal,

            // 打码量
            val bet: BigDecimal
    )

}
package com.onepiece.gpgaming.beans.value.database

import java.math.BigDecimal
import java.time.LocalDate

sealed class IntroduceDailyReportValue {

    data class IntroduceDailyReportQuery(

            val clientId: Int,

            val startDate: LocalDate,

            val endDate: LocalDate
    )

    data class IntroduceDailyReportTotal(

            val clientId: Int,

            // 会员Id
            val memberId: Int,

            val username: String,

            // 日期
            val day: String,

            // 注册量
            val registerCount: Int,

            // 首充人数
            val firstDepositCount: Int,

            // 佣金
            val commissions: BigDecimal
    )

}
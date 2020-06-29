package com.onepiece.gpgaming.beans.value.database

import java.time.LocalDate

sealed class SaleMonthReportValue {

    data class SaleMonthReportQuery(

            // bossId
            val bossId: Int,

            // clientId
            val clientId: Int,

            // 会员Id
            val memberId: Int? = null,

            // 电销Id
            val saleId: Int? = null,

            val saleUsername: String? = null,

            // 开始日期
            val startDate: LocalDate,

            // 截至日期
            val endDate: LocalDate

    )

}
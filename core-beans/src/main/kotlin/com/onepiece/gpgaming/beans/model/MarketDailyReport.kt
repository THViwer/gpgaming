package com.onepiece.gpgaming.beans.model

import java.math.BigDecimal
import java.time.LocalDate

data class MarketDailyReport(

        // id
        val id: Int,

        // clientId
        val clientId: Int,

        // 日期
        val day: LocalDate,

        // 营销Id
        val marketId:  Int,

        // 注册量
        val registerCount: Int,

        // 访问量
        val viewCount:  Int,

        // 充值金额
        val depositAmount: BigDecimal,

        // 取款金额
        val withdrawAmount:  BigDecimal,

        // 打码量
        val bet: BigDecimal

)
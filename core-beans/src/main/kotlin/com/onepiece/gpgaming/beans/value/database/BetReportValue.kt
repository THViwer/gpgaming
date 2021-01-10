package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Platform
import java.math.BigDecimal

sealed class BetReportValue {

    data class MBetReport(

            // 业主id
            val clientId: Int,

            // 会员Id
            val memberId: Int,

            // 平台
            val platform: Platform,

            // 下注金额
            val totalBet: BigDecimal,

            val betCount: Int,

            // 有效投注
            val validBet: BigDecimal,

            // 玩家盈利金额s
            val payout: BigDecimal
    )

    data class CBetReport(

            // 业主id
            val clientId: Int,

            // 下注金额
            val totalBet: BigDecimal,

            // 玩家盈利金额s
            val payout: BigDecimal
    )

}
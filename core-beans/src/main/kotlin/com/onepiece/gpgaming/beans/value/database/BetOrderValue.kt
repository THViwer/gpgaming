package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDateTime

sealed class BetOrderValue {

    data class BetOrderQuery(
            
            val clientId: Int,

            val memberId: Int,

            val platform: Platform,

            val betStartTime: LocalDateTime,

            val betEndTime: LocalDateTime
    )

    data class BetOrderCo(
            // 厅主
            val clientId: Int,

            // 会员Id
            val memberId: Int,

            // 平台
            val platform: Platform,

            // 订单Id
            val orderId: String,

            // 下注金额
            val betAmount: BigDecimal,

            // 有效投注
            val validAmount: BigDecimal,

            // 派彩
            val payout: BigDecimal,

            // 原始订单数据(json格式)
            val originData: String,

            // 下注时间
            val betTime: LocalDateTime,

            // 结算时间
            val settleTime: LocalDateTime
    )

    data class BetMarkVo(

            val id: Int,

            val clientId: Int,

            val memberId: Int,

            val platform: Platform,

            val betAmount: BigDecimal,

            val payout: BigDecimal

    ) {
        val winAmount: BigDecimal = payout.minus(betAmount)
    }


}
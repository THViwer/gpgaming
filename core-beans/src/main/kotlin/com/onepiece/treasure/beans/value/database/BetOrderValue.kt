package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDateTime

sealed class BetOrderValue {

    data class BetOrderCo(
            // 厅主
            val clientId: Int,

            // 会员Id
            val memberId: Int,

            // 订单Id
            val orderId: String,

            // 平台
            val platform: Platform,

            // 下注金额
            val betAmount: BigDecimal,

            // 获得金额
            val winAmount: BigDecimal,

            // 标记已处理打码量
            val mark: Boolean,

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

            val winAmount: BigDecimal

    )




}
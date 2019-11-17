package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 下注订单
 */
data class BetOrder(

        // id
        val id: Int,

        // 订单Id
        val orderId: String,

        // 厅主
        val clientId: Int,

        // 平台
        val platform: Platform,

        // 下注金额
        val betAmount: BigDecimal,

        // 获得金额
        val winAmount: BigDecimal,

        // 结算金额 对玩家 +(玩家赢钱,厅主输钱) -(玩家数钱，厅主赢钱)
        val money: BigDecimal,

        // 原始订单数据(json格式)
        val originData: String,

        // 下注时间
        val betTime: LocalDateTime,

        // 结算时间
        val settleTime: LocalDateTime

)
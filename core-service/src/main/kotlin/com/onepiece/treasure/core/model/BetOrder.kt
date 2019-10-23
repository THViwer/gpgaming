package com.onepiece.treasure.core.model

import com.onepiece.treasure.core.model.enums.BetState
import com.onepiece.treasure.core.model.enums.Platforms
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

        // 订单状态
        val state: BetState,

        // 平台
        val platform: Platforms,

        // 下注金额
        val betMoney: BigDecimal,

        // 结算金额 对玩家 +(玩家赢钱,厅主输钱) -(玩家数钱，厅主赢钱)
        val money: BigDecimal,

        // 创建时间
        val createdTime: LocalDateTime,

        // 结算时间
        val settleTime: LocalDateTime?

)
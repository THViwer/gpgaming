package com.onepiece.treasure.core.model

import com.onepiece.treasure.core.model.enums.OrderState
import com.onepiece.treasure.core.model.enums.Platform
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 平台钱包转账
 */
data class PlatformTransfer(

        // 订单Id
        val orderId: String,

        // 厅主Id
        val clientId: Int,

        // 平台
        val platform: Platform,

        // 平台钱包Id
        val platformWalletId: Int,

        // 转账流程 1: 主钱包 -> 游戏钱包 2：游戏钱包 -> 主钱包
        val process: Int,

        // 转账金额(转账金额+优惠金额)
        val balance: BigDecimal,

        // 优惠金额
        val giftBalance: BigDecimal,

        // 打码量要求
        val streamFlow: BigDecimal,

        // 订单状态
        val state: OrderState,

        // 订单状态
        val createdTime: LocalDateTime
)
package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.TransferState
import java.math.BigDecimal

data class TransferOrderCo(
        // 订单Id
        val orderId: String,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 转账金额
        val money: BigDecimal,

        // 赠送金额
        val giftMoney: BigDecimal,

        // 转出平台
        val from: Platform,

        // 转入平台
        val to: Platform
)

data class TransferOrderUo(
        val orderId: String,

        val state: TransferState
)
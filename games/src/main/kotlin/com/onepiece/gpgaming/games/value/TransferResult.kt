package com.onepiece.gpgaming.games.value

import java.math.BigDecimal

data class TransferResult(

        // 订单Id
        val orderId: String,

        // 平台订单Id
        val platformOrderId: String,

        // 余额
        val balance: BigDecimal,

        // 变更之后余额
        val afterBalance: BigDecimal
)
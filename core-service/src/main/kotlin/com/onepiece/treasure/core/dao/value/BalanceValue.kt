package com.onepiece.treasure.core.dao.value

import java.math.BigDecimal

data class BalanceCo(
        // 厅主Id
        val clientId: Int

)

data class BalanceUo(

        // 厅主Id
        val clientId: Int,

        // 操作金额
        val money: BigDecimal
)
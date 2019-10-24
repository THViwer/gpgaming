package com.onepiece.treasure.core.dao.value

import java.math.BigDecimal

data class WalletCo(

        val clientId: Int,

        val memberId: Int
)

data class WalletUo(

        val clientId: Int,

        val memberId: Int,

        val money: BigDecimal,

        val addTotalMoney: BigDecimal,

        val giftMoney: BigDecimal
)
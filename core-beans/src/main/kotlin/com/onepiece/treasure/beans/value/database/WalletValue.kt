package com.onepiece.treasure.beans.value.database

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
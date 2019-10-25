package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.WalletEvent
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

        val giftMoney: BigDecimal,

        val event: WalletEvent,

        val remarks: String
)
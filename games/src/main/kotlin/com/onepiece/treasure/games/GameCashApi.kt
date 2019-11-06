package com.onepiece.treasure.games

import com.onepiece.treasure.games.value.TransferResult
import java.math.BigDecimal

abstract class GameCashApi {

    abstract fun wallet(username: String): BigDecimal

    open fun clientBalance(): BigDecimal {
        return BigDecimal.valueOf(-1)
    }

    abstract fun transfer(username: String, orderId: String, money: BigDecimal): TransferResult


}
package com.onepiece.treasure.games

import com.onepiece.treasure.games.value.TransferResult
import java.math.BigDecimal

interface GameCashApi {

    fun wallet(username: String): BigDecimal

    fun clientBalance(): BigDecimal

    fun transfer(username: String, orderId: String, money: BigDecimal): TransferResult


}
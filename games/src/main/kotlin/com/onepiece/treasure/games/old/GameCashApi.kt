package com.onepiece.treasure.games.old

import com.onepiece.treasure.games.value.ClientAuthVo
import com.onepiece.treasure.games.value.TransferResult
import java.math.BigDecimal

abstract class GameCashApi {

    abstract fun wallet(clientAuthVo: ClientAuthVo? = null, username: String): BigDecimal

    open fun clientBalance(clientAuthVo: ClientAuthVo? = null): BigDecimal {
        return BigDecimal.valueOf(-1)
    }

    abstract fun transfer(clientAuthVo: ClientAuthVo? = null, username: String, orderId: String, money: BigDecimal): TransferResult


}
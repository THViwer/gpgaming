package com.onepiece.gpgaming.utils

import java.math.BigDecimal
import java.math.BigDecimal.ROUND_CEILING

object BalanceUtil {

    fun format(balance: Double): BigDecimal {
        return BigDecimal.valueOf(balance).setScale(2, ROUND_CEILING)
    }

}
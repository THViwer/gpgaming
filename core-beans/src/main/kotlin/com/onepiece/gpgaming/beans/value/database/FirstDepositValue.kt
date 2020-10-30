package com.onepiece.gpgaming.beans.value.database

import java.math.BigDecimal

sealed class FirstDepositValue {

    data class FirstDepositFrequencyVo(
            val clientId: Int,

            val memberId: Int
    )

    data class FirstDepositVo(
            val clientId: Int,

            val totalDeposit: BigDecimal
    )

}
package com.onepiece.treasure.controller.value

import java.math.BigDecimal
import java.util.*

sealed class PlatformAuthValue {

    data class SpadeGamingRequest(

            val accId: String,

            val language: String,

            val merchantCode: String,

            val token: String,

            val serialNo: String
    )

    data class SpadeGamingResponse(

            val merchantCode: String,

            val msg: String,

            val code: Int = 0,

            val serialNo: String = UUID.randomUUID().toString(),

            val acctInfo:AcctInfo

    ) {
        data class AcctInfo(
                val acctId: String,

                val balance: BigDecimal = BigDecimal.ZERO,

                val userName: String,

                val currency: String = "MYR",

                val siteId: String

        )
    }

}
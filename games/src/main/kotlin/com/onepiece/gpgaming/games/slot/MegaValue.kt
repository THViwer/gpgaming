package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.games.bet.JacksonMapUtil

sealed class MegaValue {

    data class Result(

            val error: String?,

            val jsonrpc: String

    ): JacksonMapUtil()

}
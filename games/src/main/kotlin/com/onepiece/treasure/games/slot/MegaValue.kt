package com.onepiece.treasure.games.slot

import com.onepiece.treasure.games.bet.JacksonMapUtil

sealed class MegaValue {

    data class Result(

            val error: String?,

            val jsonrpc: String

    ): JacksonMapUtil()

}
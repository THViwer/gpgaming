package com.onepiece.treasure.games.slot

import com.onepiece.treasure.games.bet.JacksonMapUtil

sealed class PragmaticValue {

    data class Result(
            val error: Int,

            val description: String

    ): JacksonMapUtil()

}
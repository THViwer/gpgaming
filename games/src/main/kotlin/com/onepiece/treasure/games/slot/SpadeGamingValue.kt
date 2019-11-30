package com.onepiece.treasure.games.slot

import com.onepiece.treasure.games.bet.JacksonMapUtil

sealed class SpadeGamingValue {

    data class Result(
            val code: Int,

            val msg: String

    ): JacksonMapUtil()

}
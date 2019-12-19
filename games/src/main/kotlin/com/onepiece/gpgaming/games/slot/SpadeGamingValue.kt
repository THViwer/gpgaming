package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.games.bet.JacksonMapUtil

sealed class SpadeGamingValue {

    data class Result(
            val code: Int,

            val msg: String

    ): JacksonMapUtil()

}
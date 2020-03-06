package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.games.bet.JacksonMapUtil

sealed class PragmaticValue {

    data class Result(
            val error: Int

//            val description: String

    ): JacksonMapUtil()

}
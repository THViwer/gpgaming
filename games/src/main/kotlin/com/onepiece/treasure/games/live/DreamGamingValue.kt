package com.onepiece.treasure.games.live

import com.onepiece.treasure.games.bet.JacksonMapUtil

sealed class DreamGamingValue {

    data class Result(
            val codeId: Int,

            val token: String,

            val random: String

    ): JacksonMapUtil()

}
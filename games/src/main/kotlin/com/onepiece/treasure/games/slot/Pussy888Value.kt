package com.onepiece.treasure.games.slot

import com.onepiece.treasure.games.bet.JacksonMapUtil

sealed class Pussy888Value {

    data class Result(
            val msg: String,

            val success: Boolean

    ): JacksonMapUtil()

}
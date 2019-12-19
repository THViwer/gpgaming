package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.games.bet.JacksonMapUtil

sealed class Pussy888Value {

    data class Result(
            val msg: String,

            val success: Boolean

    ): JacksonMapUtil()

}
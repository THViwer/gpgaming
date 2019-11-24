package com.onepiece.treasure.games.live

import com.onepiece.treasure.games.bet.JacksonMapUtil

sealed class SpadeGamingValue {

    data class Result(
            val serialNo: String,

            val code: Int,

            val msg: String

    ): JacksonMapUtil()

}
package com.onepiece.treasure.games.live

import com.onepiece.treasure.games.bet.JacksonMapUtil

sealed class SexyGamingValue {

    data class Result(

            val status: String,

            val desc: String?
    ): JacksonMapUtil()

}
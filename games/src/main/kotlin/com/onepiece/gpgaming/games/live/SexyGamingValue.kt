package com.onepiece.gpgaming.games.live

import com.onepiece.gpgaming.games.bet.JacksonMapUtil

sealed class SexyGamingValue {

    data class Result(

            val status: String,

            val desc: String?
    ): JacksonMapUtil()

}
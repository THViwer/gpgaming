package com.onepiece.gpgaming.games.live

import com.onepiece.gpgaming.games.bet.JacksonMapUtil

sealed class EBetValue {

    data class Result(
            val status: String,

            val apiVersion: String

    ): JacksonMapUtil()

}
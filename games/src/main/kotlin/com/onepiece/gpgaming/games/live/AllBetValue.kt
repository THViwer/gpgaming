package com.onepiece.gpgaming.games.live

import com.fasterxml.jackson.annotation.JsonProperty
import com.onepiece.gpgaming.games.bet.JacksonMapUtil

sealed class AllBetValue {


    data class Result(
            @JsonProperty("error_code")
            val errorCode: String,

            val message: String?

    ): JacksonMapUtil()

}
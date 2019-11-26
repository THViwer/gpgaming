package com.onepiece.treasure.games.live

import com.fasterxml.jackson.annotation.JsonProperty
import com.onepiece.treasure.games.bet.JacksonMapUtil

sealed class FggValue {

    data class Result(

            @JsonProperty("ErrorCode")
            val errorCode: String = "",

            @JsonProperty("ErrorDesc")
            val errorDesc: String = ""

    ): JacksonMapUtil()
}
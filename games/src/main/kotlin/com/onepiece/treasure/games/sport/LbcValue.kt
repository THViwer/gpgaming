package com.onepiece.treasure.games.sport

import com.fasterxml.jackson.annotation.JsonProperty
import com.onepiece.treasure.games.bet.JacksonMapUtil

sealed class LbcValue {

    data class Result(

            @JsonProperty("error_code")
            val errorCode: Int,


            val message: String
    ): JacksonMapUtil()

}
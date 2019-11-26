package com.onepiece.treasure.games.sport

import com.fasterxml.jackson.annotation.JsonProperty
import com.onepiece.treasure.games.bet.JacksonMapUtil

sealed class CMDValue {

    data class Result(

            @JsonProperty("Code")
            val code: Int

    ): JacksonMapUtil()

}
package com.onepiece.gpgaming.games.sport

import com.fasterxml.jackson.annotation.JsonProperty
import com.onepiece.gpgaming.games.bet.JacksonMapUtil

sealed class CMDValue {

    data class Result(

            @JsonProperty("Code")
            val code: Int

    ): JacksonMapUtil()

}
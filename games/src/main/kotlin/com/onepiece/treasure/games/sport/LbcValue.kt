package com.onepiece.treasure.games.sport

import com.fasterxml.jackson.annotation.JsonProperty

sealed class LbcValue {

    data class Result(

            @JsonProperty("error_code")
            val errorCode: Int,


            val message: String
    )

}
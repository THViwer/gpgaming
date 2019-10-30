package com.onepiece.treasure.games.joker.value

import com.fasterxml.jackson.annotation.JsonProperty

data class JokerRegisterResult(

        @JsonProperty("Status")
        val status: String,

        @JsonProperty("Data")
        val data: JokerRegisterResultVo


)

data class JokerRegisterResultVo(

        @JsonProperty("Username")
        val username: String,

        @JsonProperty("Status")
        val status: String
)
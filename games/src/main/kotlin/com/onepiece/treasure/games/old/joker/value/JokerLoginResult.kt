package com.onepiece.treasure.games.old.joker.value

import com.fasterxml.jackson.annotation.JsonProperty

data class JokerLoginResult(

        @JsonProperty("Username")
        val username: String,

        @JsonProperty("Token")
        val token: String
)
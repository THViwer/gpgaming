package com.onepiece.treasure.games.joker.value

import com.fasterxml.jackson.annotation.JsonProperty

data class JokerRegisterResult(

        @JsonProperty("Status")
        val status: String

)
package com.onepiece.treasure.games.joker.value

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class JokerWalletResult(

        @JsonProperty("Username")
        val username: String,

        @JsonProperty("Credit")
        val credit: BigDecimal
)
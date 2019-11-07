package com.onepiece.treasure.games.old.joker.value

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class JokerWalletResult(

        @JsonProperty("Username")
        val username: String,

        @JsonProperty("Credit")
        val credit: BigDecimal,

        @JsonProperty("FreeCredit")
        val freeCredit: BigDecimal
)
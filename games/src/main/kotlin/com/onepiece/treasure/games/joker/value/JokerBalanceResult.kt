package com.onepiece.treasure.games.joker.value

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class JokerBalanceResult(

        @JsonProperty("Amount")
        val amount: BigDecimal
)
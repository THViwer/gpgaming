package com.onepiece.treasure.games.old.joker.value

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class JokerTransferResult(

        @JsonProperty("Username")
        val username: String,

        @JsonProperty("Credit")
        val credit: BigDecimal,

        @JsonProperty("RequestID")
        val requestId: String,

        @JsonProperty("Time")
        val time: String,

        @JsonProperty("BeforeCredit")
        val beforeCredit: BigDecimal
)
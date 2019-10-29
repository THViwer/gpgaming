package com.onepiece.treasure.games.joker.value

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

data class JokerBetOrderVo(

        @JsonProperty("OCode")
        val oCode: String,

        @JsonProperty("Username")
        val username: String,

        @JsonProperty("GameCode")
        val gameCode: String,

        @JsonProperty("Description")
        val description: String,

        @JsonProperty("Type")
        val type: String,

        @JsonProperty("Amount")
        val Amount: BigDecimal,

        @JsonProperty("Result")
        val result: BigDecimal,

        // 2016-12-01T23:40:00.455827+08
        @JsonProperty("Time") //TODO 暂时用
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'+08'",  timezone = "GMT+8")
        val time: LocalDateTime,

        @JsonProperty("AppID")
        val appId: String
)

data class BetResult(

        val data: Map<String, List<JokerBetOrderVo>>,

        val nextId: String
)

package com.onepiece.treasure.games.joker.value

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.math.BigDecimal
import java.time.ZonedDateTime

data class JokerBetOrderVo(

        @JsonProperty("OCode")
        val oCode: String,

        @JsonProperty("Username")
        val username: String,

        @JsonProperty("GameCode")
        val gameCode: String,

        @JsonProperty("Description")
        val description: String,

        @JsonProperty("RoundID")
        val roundId: String,


        @JsonProperty("Amount")
        val Amount: BigDecimal,

        @JsonProperty("FreeAmount")
        val freeAmount: BigDecimal,

        @JsonProperty("Result")
        val result: BigDecimal,

        // 2016-12-01T23:40:00.455827+08
        @JsonProperty("Time") //TODO 暂时用
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",  timezone = "GMT+8")
        val time: ZonedDateTime,

        @JsonProperty("Details")
        val details: String?,

        @JsonProperty("AppID")
        val appId: String,

        @JsonProperty("CurrencyCode")
        val currencyCode: String,

        @JsonProperty("Type")
        val type: String

)

data class BetResult(

        val data: Map<String, List<JokerBetOrderVo>>,

        val nextId: String,

        val games: List<BetGame>
)

data class BetGame(
        @JsonProperty("GameCode")
        val gameCode: String,

        @JsonProperty("GameName")
        val gameName: String,

        @JsonProperty("GameType")
        val gameType: String
)

fun main() {
        val json = "{\"data\":{\"Game\":[{\"OCode\":\"ro3uxhph9xrza\",\"Username\":\"FC51\",\"GameCode\":\"fwria11mjbrwh\",\"Description\":\"Main Scroll\",\"RoundID\":\"ro3uxhph9xrza\",\"Amount\":0.54,\"FreeAmount\":0.0,\"Result\":0.0,\"Time\":\"2019-10-30T18:38:10.4914228+08:00\",\"Details\":null,\"AppID\":\"F1S8\",\"CurrencyCode\":\"MYR\",\"Type\":\"Main\"},{\"OCode\":\"qptft86uu4pn4\",\"Username\":\"FC51\",\"GameCode\":\"fwria11mjbrwh\",\"Description\":\"Main Scroll\",\"RoundID\":\"qptft86uu4pn4\",\"Amount\":0.54,\"FreeAmount\":0.0,\"Result\":0.0,\"Time\":\"2019-10-30T18:47:35.7740157+08:00\",\"Details\":null,\"AppID\":\"F1S8\",\"CurrencyCode\":\"MYR\",\"Type\":\"Main\"},{\"OCode\":\"qptf6rh6u4pn4\",\"Username\":\"FC51\",\"GameCode\":\"fwria11mjbrwh\",\"Description\":\"Main Scroll\",\"RoundID\":\"qptf6rh6u4pn4\",\"Amount\":0.54,\"FreeAmount\":0.0,\"Result\":0.0,\"Time\":\"2019-10-30T18:47:40.9688248+08:00\",\"Details\":null,\"AppID\":\"F1S8\",\"CurrencyCode\":\"MYR\",\"Type\":\"Main\"},{\"OCode\":\"xjs5skf4uft7c\",\"Username\":\"FC51\",\"GameCode\":\"fwria11mjbrwh\",\"Description\":\"Main Scroll\",\"RoundID\":\"xjs5skf4uft7c\",\"Amount\":0.54,\"FreeAmount\":0.0,\"Result\":0.9,\"Time\":\"2019-10-30T18:47:45.7736333+08:00\",\"Details\":null,\"AppID\":\"F1S8\",\"CurrencyCode\":\"MYR\",\"Type\":\"Main\"},{\"OCode\":\"qptf95cqu4pn4\",\"Username\":\"FC51\",\"GameCode\":\"fwria11mjbrwh\",\"Description\":\"Main Scroll\",\"RoundID\":\"qptf95cqu4pn4\",\"Amount\":0.54,\"FreeAmount\":0.0,\"Result\":0.0,\"Time\":\"2019-10-30T18:48:17.1920884+08:00\",\"Details\":null,\"AppID\":\"F1S8\",\"CurrencyCode\":\"MYR\",\"Type\":\"Main\"}]},\"nextId\":\"eyJHYW1lIjp7IklEIjoiRXh0OkdhbWU6RjFTOE06MjAxOTEwMzAxODQ4IiwiQ291bnQiOjEsIk5leHRJRCI6MH0sIlByaXplIjp7IklEIjoiIiwiQ291bnQiOjAsIk5leHRJRCI6MH0sIkFnIjp7IklEIjoiIiwiQ291bnQiOjAsIk5leHRJRCI6MH0sIlNhIjp7IklEIjoiIiwiQ291bnQiOjAsIk5leHRJRCI6MH0sIkFsbEJldCI6eyJJRCI6IiIsIkNvdW50IjowLCJOZXh0SUQiOjB9LCJFdm8iOnsiSUQiOiIiLCJDb3VudCI6MCwiTmV4dElEIjowfSwiRUJldCI6eyJJRCI6IiIsIkNvdW50IjowLCJOZXh0SUQiOjB9LCJQcmFnbWF0aWMiOnsiSUQiOiIiLCJDb3VudCI6MCwiTmV4dElEIjowfSwiSkwiOnsiSUQiOiIiLCJDb3VudCI6MCwiTmV4dElEIjowfSwiQm9QaW5nIjp7IklEIjoiIiwiQ291bnQiOjAsIk5leHRJRCI6MH0sIkZsYXNoVGVjaCI6eyJJRCI6IiIsIkNvdW50IjowLCJOZXh0SUQiOjB9LCJTQk9CZXQiOnsiSUQiOiIiLCJDb3VudCI6MCwiTmV4dElEIjowfX0=\",\"games\":[{\"GameCode\":\"fwria11mjbrwh\",\"GameName\":\"Three Kingdoms Quest\",\"GameType\":\"Slot\"}]}"

        val mapper = jacksonObjectMapper()
                .registerModule(Jdk8Module())
                .registerModule(JavaTimeModule())

        val result = mapper.readValue<BetResult>(json)
        println(result)
}

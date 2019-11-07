package com.onepiece.treasure.games.slot.joker

import com.fasterxml.jackson.annotation.JsonProperty
import com.onepiece.treasure.games.old.joker.value.BetGame
import java.math.BigDecimal
import java.time.ZonedDateTime

sealed class JokerValue {

    data class GetCreditResult(

            @JsonProperty("Username")
            val username: String,

            @JsonProperty("Credit")
            val credit: BigDecimal,

            @JsonProperty("FreeCredit")
            val freeCredit: BigDecimal
    )

    data class TransferCreditResult(

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


    data class RequestUserTokenResult(

            @JsonProperty("Username")
            val username: String,

            @JsonProperty("Token")
            val token: String
    )

    data class BetResult(

            val data: Map<String, List<JokerBetOrderVo>>,

            val nextId: String,

            val games: List<BetGame>
    )

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
            val amount: BigDecimal,

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


    data class GetMembersWinLoss(

            @JsonProperty("Username")
            val username: String,

            @JsonProperty("OCode")
            val oCode: String,

            @JsonProperty("Amount")
            val amount: BigDecimal,

            @JsonProperty("Result")
            val result: BigDecimal
    )


}
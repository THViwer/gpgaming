package com.onepiece.treasure.games.old.joker.value

import com.fasterxml.jackson.annotation.JsonProperty

data class JokerSlotGameResult(
        @JsonProperty("ListGames")
        val listGames: List<JokerSlotGame>
)

data class JokerSlotGame(

        @JsonProperty("GameType")
        val gameType: String,

        @JsonProperty("Code")
        val code: String,

        @JsonProperty("GameOCode")
        val gameOCode: String,

        @JsonProperty("GameCode")
        val gameCode: String,

        @JsonProperty("GameName")
        val gameName: String,

        @JsonProperty("SupportedPlatForms")
        val supportedPlatForms: String,

        @JsonProperty("Specials")
        val specials: String?,

        @JsonProperty("Technology")
        val technology: String,

        @JsonProperty("Order")
        val order: Int,

        @JsonProperty("DefaultWidth")
        val defaultWidth: Int,

        @JsonProperty("DefaultHeight")
        val defaultHeight: Int,

        @JsonProperty("Image1")
        val image1: String,

        @JsonProperty("Image2")
        val image2: String
)
package com.onepiece.treasure.games.joker.value

import com.fasterxml.jackson.annotation.JsonProperty

data class JokerSlotGame(

        @JsonProperty("GameType")
        val gameType: String,

        @JsonProperty("GameCode")
        val gameCode: String,

        @JsonProperty("GameName")
        val gameName: String,

        @JsonProperty("SupportedPlatForms")
        val supportedPlatForms: String,

        @JsonProperty("Special")
        val special: String,

        @JsonProperty("DefaultWidth")
        val defaultWidth: Int,

        @JsonProperty("DefaultHeight")
        val defaultHeight: Int,

        @JsonProperty("Image1")
        val image1: String
)
package com.onepiece.treasure.games.slot

import com.fasterxml.jackson.annotation.JsonProperty
import com.onepiece.treasure.games.bet.JacksonMapUtil
import com.onepiece.treasure.games.bet.MapUtil

sealed class JokerValue {

    data class Result(
            @JsonProperty("Status")
            val status: String?

    ): JacksonMapUtil()

    data class GameResult(
            @JsonProperty("ListGames")
            val list: List<Map<String, Any>>
    ) {

        val games: List<MapUtil>
            get() {
                return list.map { MapUtil.instance(it) }
            }


    }



}
package com.onepiece.gpgaming.games.slot

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.onepiece.gpgaming.games.bet.JacksonMapUtil
import com.onepiece.gpgaming.games.bet.MapUtil

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

    data class BetResult(
            val nextId: String,

            @JsonIgnore
            @JsonAnySetter
            val data: Map<String, Any> = hashMapOf()
    ) {
        val mapUtil: MapUtil
            @JsonIgnore
            get() {
                return MapUtil.instance(data)
            }
    }



}
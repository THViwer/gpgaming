package com.onepiece.treasure.games.live

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore

sealed class EvolutionValue {

    data class Result(

            @JsonIgnore
            @JsonAnySetter
            val data: Map<String, Any> = hashMapOf()
    )

    data class BetResult(

            val uuid: String,

            val timestamp: String,

            val data: List<Data>

    ) {

        data class Data(
                val date: String,

                val games: List<BetVo>

        ) {

            data class BetVo(
                    @JsonIgnore
                    @JsonAnySetter
                    val data: Map<String, Any> = hashMapOf()
            )

        }

    }

}
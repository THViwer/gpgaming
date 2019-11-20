package com.onepiece.treasure.games.live

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore

sealed class EvolutionValue {

    data class Result(

            @JsonIgnore
            @JsonAnySetter
            val data: Map<String, Any> = hashMapOf()
    )

}
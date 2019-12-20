package com.onepiece.gpgaming.games.combination

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.games.bet.MapUtil

sealed class PlaytechValue {

    data class Result(
            val code: Int,

            val message: String,

            @JsonIgnore
            @JsonAnySetter
            val resultData: Map<String, Any> = hashMapOf()
    ) {

        val mapUtil: MapUtil
            @JsonIgnore
            get() {
                return MapUtil.instance(resultData)
            }

    }

}
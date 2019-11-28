package com.onepiece.treasure.games.live

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.treasure.games.bet.MapUtil

sealed class DreamGamingValue {

    data class Result(
            val codeId: Int,

            val random: String,

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
package com.onepiece.treasure.games.slot

import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.treasure.games.bet.JacksonMapUtil
import com.onepiece.treasure.games.bet.MapUtil

sealed class TTGValue {

    data class BetResult(

            val details: List<Map<String, Any>> = emptyList()

    ): JacksonMapUtil() {

        val orders: List<MapUtil>
            @JsonIgnore
            get() = details.map { MapUtil.instance(it) }

    }

}
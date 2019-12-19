package com.onepiece.gpgaming.games.slot

import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.games.bet.JacksonMapUtil
import com.onepiece.gpgaming.games.bet.MapUtil

sealed class TTGValue {

    data class BetResult(

            val details: List<Map<String, Any>>?

    ): JacksonMapUtil() {

        val orders: List<MapUtil>
            @JsonIgnore
            get() = details?.map { MapUtil.instance(it) }?: emptyList()

    }

}
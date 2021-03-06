package com.onepiece.gpgaming.games.bet

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore

open class JacksonMapUtil(
        @JsonIgnore
        @JsonAnySetter
        val _data: Map<String, Any> = hashMapOf()
) {

    val mapUtil: MapUtil
        @JsonIgnore
        get() {
            return MapUtil.instance(_data)
        }

}
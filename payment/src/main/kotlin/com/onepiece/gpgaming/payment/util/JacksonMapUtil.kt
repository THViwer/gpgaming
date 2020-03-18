package com.onepiece.gpgaming.payment.util

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore

open class JacksonMapUtil(
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
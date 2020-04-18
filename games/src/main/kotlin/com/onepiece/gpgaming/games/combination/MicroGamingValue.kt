package com.onepiece.gpgaming.games.combination

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.games.bet.MapUtil

sealed class MicroGamingValue {

    data class OauthToken(
        val access_token :String,

        val refresh_token: String,

        val expires_in: Int,

        val scope: String?,

        val jti: String
    )

    data class Result(
            val meta: Map<String, Any>,

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
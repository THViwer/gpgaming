package com.onepiece.gpgaming.games.combination

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.games.bet.JacksonMapUtil
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

    data class BetResult(

            val code: Int,

            val message: String,

            val data: Page
    ) {

        data class Page(
            val pagination: Pagination,

            val data: List<Map<String, Any>>

        ) {

            val orders: List<MapUtil>
                @JsonIgnore
                get() =  data.map { MapUtil.instance(it) }

            data class Pagination(
                    val page: Int,

                    val pre_page: Int,

                    val last_page: Int,

                    val has_next_page: Boolean
            )

        }

    }

}
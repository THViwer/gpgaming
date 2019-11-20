package com.onepiece.treasure.games.live

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode

sealed class FggValue {

    data class Result(

            @JsonProperty("ErrorCode")
            val errorCode: String = "",

            @JsonProperty("ErrorDesc")
            val errorDesc: String = "",

            @JsonIgnore
            @JsonAnySetter
            val data: Map<String, Any> = hashMapOf()
    ) {

        @JsonIgnore
        fun checkErrorCode() {
            check(this.errorCode.isBlank()) { OnePieceExceptionCode.PLATFORM_DATA_FAIL }
        }


    }
}
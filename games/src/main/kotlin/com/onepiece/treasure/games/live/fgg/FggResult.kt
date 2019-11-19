package com.onepiece.treasure.games.live.fgg

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class FggResult(

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

        //TODO
    }


}
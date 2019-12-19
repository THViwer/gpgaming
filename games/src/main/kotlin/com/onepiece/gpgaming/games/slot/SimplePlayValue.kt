package com.onepiece.gpgaming.games.slot

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.onepiece.gpgaming.games.bet.JacksonMapUtil
import com.onepiece.gpgaming.games.bet.MapUtil

sealed class SimplePlayValue {

    @JacksonXmlRootElement(localName = "APIResponse")
    data class Result(

            @JacksonXmlProperty(localName = "ErrorMsgId")
            val errorMsgId: Int,

            @JacksonXmlProperty(localName = "ErrorMsg")
            val errorMsg: String?,

            @JsonIgnore
            @JsonAnySetter
            val data: Map<String, Any> = hashMapOf()
    ) {

        @JsonIgnore
        fun mapUtil(): MapUtil {
            return MapUtil.instance(data)
        }
    }

    @JacksonXmlRootElement(localName = "APIResponse")
    data class BetResult(

            @JacksonXmlProperty(localName = "ErrorMsgId")
            val errorMsgId: Int,

            @JacksonXmlProperty(localName = "ErrorMsg")
            val errorMsg: String?,

            @JacksonXmlProperty(localName = "BetDetailList")
            val betDetailList: List<BetResult>? = arrayListOf()
    ) {

        class BetResult: JacksonMapUtil()
    }

}
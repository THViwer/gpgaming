package com.onepiece.gpgaming.games.sport

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.onepiece.gpgaming.games.bet.MapUtil

sealed class BcsValue {

    @JacksonXmlRootElement(localName = "response")
    data class Result(

            @JacksonXmlProperty(localName = "errcode")
            val errorCode: String,

//            @JacksonXmlProperty(localName = "errtext")
//            val errorText: String = "",

            @JsonIgnore
            @JsonAnySetter
            val data: Map<String, Any> = hashMapOf()
    )

    @JacksonXmlRootElement(localName = "response")
    data class BetResult(
            @JacksonXmlProperty(localName = "errcode")
            val errorCode: String,

            @JacksonXmlProperty(localName = "errtext")
            val errtext: String?,

            @JacksonXmlProperty(localName = "result")
            val result: Bets


    ) {

        data class Bets(
                @JacksonXmlProperty(localName = "betlist")
                val betlist: List<Bet>,

                @JsonIgnore
                @JsonAnySetter
                val data: Map<String, Any> = hashMapOf()
        )

        data class Bet(
                @JsonIgnore
                @JsonAnySetter
                val data: Map<String, Any> = hashMapOf()
        ) {

            fun getMapUtil(): MapUtil {
                return MapUtil.instance(data)
            }

        }
    }

    @JacksonXmlRootElement(localName = "response")
    data class PullBetResult(
            @JacksonXmlProperty(localName = "errcode")
            val errorCode: String,

            @JacksonXmlProperty(localName = "errtext")
            val errtext: String?,

            @JacksonXmlProperty(localName = "result")
            val result: List<BetResult.Bet>?


    )

}
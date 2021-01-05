package com.onepiece.gpgaming.games.sport

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.onepiece.gpgaming.games.bet.MapUtil

sealed class BcsValue {

//    @JacksonXmlRootElement(localName = "response")
//    data class Result(
//
//            @JacksonXmlProperty(localName = "errcode")
//            val errorCode: String,
//
////            @JacksonXmlProperty(localName = "errtext")
////            val errorText: String = "",
//
//            @JsonIgnore
//            @JsonAnySetter
//            val data: Map<String, Any> = hashMapOf()
//    )
//
//    data class BetResult(
//            @JsonProperty("ErrorCode")
//            val errorCode: String,
//
//            @JsonProperty("ErrorMessage")
//            val errtext: String?,
//
//            @JsonProperty("")
//            val result: Bets
//
//
//    ) {
//
//        data class Bets(
//                @JsonProperty("betlist")
//                val betlist: List<Bet>,
//
//                @JsonIgnore
//                @JsonAnySetter
//                val data: Map<String, Any> = hashMapOf()
//        )
//
//        data class Bet(
//                @JsonIgnore
//                @JsonAnySetter
//                val data: Map<String, Any> = hashMapOf()
//        ) {
//
//            fun getMapUtil(): MapUtil {
//                return MapUtil.instance(data)
//            }
//
//        }
//    }

    data class Bet(
            @JsonIgnore
            @JsonAnySetter
            val data: Map<String, Any> = hashMapOf()
    ) {

        fun getMapUtil(): MapUtil {
            return MapUtil.instance(data)
        }

    }

//    @JacksonXmlRootElement(localName = "response")
//    data class PullBetResult(
//            @JacksonXmlProperty(localName = "errcode")
//            val errorCode: String,
//
//            @JacksonXmlProperty(localName = "errtext")
//            val errtext: String?,
//
//            @JacksonXmlProperty(localName = "result")
//            val result: List<BetResult.Bet>?
//    )

    data class PullBetResultForJson(

            @JsonProperty("ErrorCode")
            val errorCode: String,

            @JsonProperty("ErrorMessage")
            val errorMessage: String?,

            @JsonProperty( "Data")
            val result: List<Bet>?
    )

    //    @JacksonXmlRootElement(localName = "response")
//    data class OutstandingResult(
//            @JacksonXmlProperty(localName = "errcode")
//            val errorCode: String,
//
//            @JacksonXmlProperty(localName = "errtext")
//            val errtext: String?,
//
//            @JacksonXmlProperty(localName = "result")
//            val result: Result
//
//    ) {
//
//        data class Result(
//                @JacksonXmlProperty(localName = "Bets")
//                val bets: List<BetResult.Bet>
//        )
//
//    }
    data class OutstandingResultForJson(
            @JsonProperty("ErrorCode")
            val errorCode: String,

            @JsonProperty("ErrorMessage")
            val errorMessage: String?,

            @JsonProperty("Data")
            val bets: List<Bet>

    )

}
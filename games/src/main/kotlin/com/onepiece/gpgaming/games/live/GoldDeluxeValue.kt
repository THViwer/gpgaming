package com.onepiece.gpgaming.games.live

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.onepiece.gpgaming.games.bet.JacksonMapUtil

sealed class GoldDeluxeValue {

    data class Result(

            @JacksonXmlProperty(localName = "Header")
            val header: Header,

            @JacksonXmlProperty(localName = "Param")
            val param: Param,

            @JsonIgnore
            @JsonAnySetter
            val data: Map<String, Any> = hashMapOf()
    ) {

        data class Header(

                @JacksonXmlProperty(localName = "Method")
                val method: String = "",

                @JacksonXmlProperty(localName = "ErrorCode")
                val errorCode: String = "",

                @JacksonXmlProperty(localName = "MerchantID")
                val merchantID: String = "",

                @JacksonXmlProperty(localName = "MessageID")
                val messageID: String = ""
        )

        data class Param(
                @JsonIgnore
                @JsonAnySetter
                val data: Map<String, Any> = hashMapOf()
        )

    }

    data class BetResult(
            @JacksonXmlProperty(localName = "Header")
            val header: Result.Header,

            @JacksonXmlProperty(localName = "Param")
            val param: Param
    ) {
            data class Param(

                    @JacksonXmlProperty(localName = "TotalRecord")
                    val totalRecord: Int = 0,

                    @JacksonXmlProperty(localName = "ErrorDesc")
                    val errorDesc: String?,

                    @JacksonXmlProperty(localName = "BetInfo")
                    @JacksonXmlElementWrapper(useWrapping = false)
                    val betInfoList: List<BetInfo> = emptyList()


            ) {

                class BetInfo: JacksonMapUtil()

            }

    }

}
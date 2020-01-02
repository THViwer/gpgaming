package com.onepiece.gpgaming.games.combination

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

sealed class AsiaGamingValue {

    data class Result(

            @JacksonXmlProperty(localName = "info")
            val info: String,

            @JacksonXmlProperty(localName = "msg")
            val msg: String

    )


}
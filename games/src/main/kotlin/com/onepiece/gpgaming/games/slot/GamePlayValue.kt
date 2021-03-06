package com.onepiece.gpgaming.games.slot

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.onepiece.gpgaming.games.bet.JacksonMapUtil

sealed class GamePlayValue {

    // <?xml version="1.0" encoding="UTF-8" standalone="yes"?><resp><error_code>0</error_code></resp>
    @JacksonXmlRootElement(localName = "resp")
    data class Result(

            @JacksonXmlProperty(localName = "localName")
            val error_code: Int

    ): JacksonMapUtil()


    @JacksonXmlRootElement(localName = "resp")
    data class BetResult(

            @JacksonXmlProperty(localName = "localName")
            val error_code: Int,

            @JacksonXmlProperty(localName = "items")
            val betDetailList: List<Item>? = arrayListOf()

    ): JacksonMapUtil() {

        class Item: JacksonMapUtil()


    }


}
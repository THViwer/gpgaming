package com.onepiece.gpgaming.games.combination

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.onepiece.gpgaming.games.bet.JacksonMapUtil


sealed class AsiaGamingValue {

    @JacksonXmlRootElement(localName = "result")
    class Result: JacksonMapUtil() {

        val info: String
            get() = mapUtil.asString("info")

        val msg: String
            get() = mapUtil.asString("msg")

    }

    @JacksonXmlRootElement(localName = "result")
    data class BetResult(

            @JacksonXmlProperty(localName = "info")
            val info: String,

            @JacksonXmlProperty(localName = "addition")
            val addition: Addition,

            @JacksonXmlElementWrapper(useWrapping = false)
            @JacksonXmlProperty(localName = "row")
            val row: List<JacksonMapUtil> = emptyList()

    ): JacksonMapUtil() {

        open class Order(

        ): JacksonMapUtil()


        data class Addition(

                @JacksonXmlProperty(localName = "total")
                val total: Int,

                @JacksonXmlProperty(localName = "num_per_page")
                val num_per_page: Int,

                @JacksonXmlProperty(localName = "currentpage")
                val currentpage: Int,

                @JacksonXmlProperty(localName = "totalpage")
                val totalpage: Int,

                @JacksonXmlProperty(localName = "perpage")
                val perpage: Int
        )
    }


}

//fun main() {
//
//    val xml = """
//        <?xml version="1.0" encoding="UTF-8" standalone="yes"?><result><info>0</info><row billNo="200102105904317" playName="01000009oe" gameCode="GC001201020LZ" netAmount="5" betTime="2020-01-02 10:35:38" betAmount="5" validBetAmount="5" flag="1" playType="2" currency="MYR" tableCode="C106" recalcuTime="2020-01-02 10:35:49" beforeCredit="140.800000" betIP="36.24.11.35" platformType="AGIN" remark="" round="DSP" result="" gameType="BAC" deviceType="0" /><row billNo="200102105930566" playName="01000009oe" gameCode="GC001201020M0" netAmount="5" betTime="2020-01-02 10:36:04" betAmount="5" validBetAmount="5" flag="1" playType="2" currency="MYR" tableCode="C106" recalcuTime="2020-01-02 10:36:28" beforeCredit="145.800000" betIP="36.24.11.35" platformType="AGIN" remark="" round="DSP" result="" gameType="BAC" deviceType="0" /><row billNo="200102105964734" playName="01000009oe" gameCode="GC001201020M1" netAmount="-5" betTime="2020-01-02 10:36:42" betAmount="5" validBetAmount="5" flag="1" playType="2" currency="MYR" tableCode="C106" recalcuTime="2020-01-02 10:37:12" beforeCredit="150.800000" betIP="36.24.11.35" platformType="AGIN" remark="" round="DSP" result="" gameType="BAC" deviceType="0" /><addition><total>3</total><num_per_page>500</num_per_page><currentpage>1</currentpage><totalpage>1</totalpage><perpage>3</perpage></addition></result>
//    """.trimIndent()
//    val xmlMapper = XmlMapper()
//        .registerModule(ParameterNamesModule())
//        .registerModule(Jdk8Module())
//        .registerModule(JavaTimeModule())
//
//
//    val result = xmlMapper.readValue<AsiaGamingValue.BetResult>(xml)
//    println(result)
//}
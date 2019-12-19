package com.onepiece.gpgaming.games.slot

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.onepiece.gpgaming.games.bet.MapUtil

sealed class PNGValue {

    /**
     * <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
    <s:Body>
    <RegisterUserResponse xmlns="http://playngo.com/v1"/>
    </s:Body>
    </s:Envelope>
     */
    @JacksonXmlRootElement(localName = "s:Envelope")
    class Result(

            @JsonIgnore
            @JsonAnySetter
            val data: Map<String, Any> = hashMapOf()

    ) {

        val mapUtil: MapUtil
            @JsonIgnore
            get() {
                return MapUtil.instance(data)
            }

    }

}

fun main() {

    val xml = """
        <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
            <s:Body>
                <RegisterUserResponse xmlns="http://playngo.com/v1"/>
            </s:Body>
        </s:Envelope>
    """.trimIndent()


}
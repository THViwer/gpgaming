package com.onepiece.gpgaming.games.slot

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.onepiece.gpgaming.games.bet.JacksonMapUtil

sealed class PNGValue {

    /**
     * <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
    <s:Body>
    <RegisterUserResponse xmlns="http://playngo.com/v1"/>
    </s:Body>
    </s:Envelope>
     */
    @JacksonXmlRootElement(localName = "s:Envelope")
    class Result : JacksonMapUtil()

}

//fun main() {
//
//    val xml = """
//        <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
//            <s:Body>
//                <RegisterUserResponse xmlns="http://playngo.com/v1"/>
//            </s:Body>
//        </s:Envelope>
//    """.trimIndent()
//
//
//}
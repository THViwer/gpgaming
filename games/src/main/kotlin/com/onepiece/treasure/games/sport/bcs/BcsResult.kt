package com.onepiece.treasure.games.sport.bcs

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "response")
data class BcsResult(

        @JacksonXmlProperty(localName = "errorcode")
        val errorCode: String,

        @JacksonXmlProperty(localName = "errortext")
        val errorText: String = "",

        @JsonIgnore
        @JsonAnySetter
        val data: Map<String, Any> = hashMapOf()


)
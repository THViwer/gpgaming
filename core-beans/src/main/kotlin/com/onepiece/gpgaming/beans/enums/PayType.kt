package com.onepiece.gpgaming.beans.enums

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.model.pay.M3PayConfig
import com.onepiece.gpgaming.beans.model.pay.PayConfig

enum class PayType(val logo: String){

    M3Pay("https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/pussy888.png");

    fun readConfig(data: String, objectMapper: ObjectMapper): PayConfig {
        return when  (this) {
            M3Pay -> objectMapper.readValue<M3PayConfig>(data)
            else -> error("不支持类型")
        }


    }

}
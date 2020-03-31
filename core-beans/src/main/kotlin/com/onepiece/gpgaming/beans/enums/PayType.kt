package com.onepiece.gpgaming.beans.enums

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.model.pay.M3PayConfig
import com.onepiece.gpgaming.beans.model.pay.PayConfig

enum class PayType(
        val greyLogo: String,
        val logo: String
){

    M3Pay(greyLogo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/m3pay_gray.png",
            logo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/m3pay_ori.png"
    );

    fun readConfig(data: String, objectMapper: ObjectMapper): PayConfig {
        return when  (this) {
            M3Pay -> objectMapper.readValue<M3PayConfig>(data)
            else -> error("不支持类型")
        }


    }

}
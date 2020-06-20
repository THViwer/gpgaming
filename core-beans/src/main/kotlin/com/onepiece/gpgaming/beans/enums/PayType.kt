package com.onepiece.gpgaming.beans.enums

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.model.pay.GPPayConfig
import com.onepiece.gpgaming.beans.model.pay.M3PayConfig
import com.onepiece.gpgaming.beans.model.pay.PayConfig
import com.onepiece.gpgaming.beans.model.pay.SurePayConfig

enum class PayType(
        val greyLogo: String,
        val logo: String,
        val sort: Int
){

    M3Pay(greyLogo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/m3pay_gray.png",
            logo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/m3pay_ori.png",
            sort = 100
    ),

    SurePay(greyLogo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/SurePay_gray.png",
            logo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/SurePay_ori.png",
            sort = 100
    ),

    FPX(greyLogo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/FPXpay_gray.png",
            logo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/FPXpay_ori.png",
            sort = 1
    )
    ;


    fun readConfig(data: String, objectMapper: ObjectMapper): PayConfig {
        return when  (this) {
            M3Pay -> objectMapper.readValue<M3PayConfig>(data)
            SurePay -> objectMapper.readValue<SurePayConfig>(data)
            FPX -> objectMapper.readValue<GPPayConfig>(data)
        }
    }

}
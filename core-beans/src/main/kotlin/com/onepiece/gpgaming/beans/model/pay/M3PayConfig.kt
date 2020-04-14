package com.onepiece.gpgaming.beans.model.pay

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class M3PayConfig(

        // val merchant: String = "merchant.m3pay.com",
        // id
        // val id: String = "T004",
        // 密码
        // val pwd: String = "20200316_T004",

        // 支付api
        val apiPath: String = "http://payment.m3pay.com/epayment/entry.aspx",

        // 商户code
        val memberCode: String = "T004",

        // 商户密钥
        val merchantKey: String = "dbb06d88-38ae-4aa5-a49a-2f77ce31efd8",

        // 支付通知地址
        val backendURL: String  = "https://open.gpgaming88.com/api/v1/player/pay/m3pay"

) : PayConfig

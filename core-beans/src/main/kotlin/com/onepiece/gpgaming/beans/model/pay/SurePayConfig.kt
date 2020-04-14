package com.onepiece.gpgaming.beans.model.pay

data class SurePayConfig(

        // api地址
        val apiPath: String = "https://pgw.surepay88.coms",

        // 商户id
        val merchantId: String = "KennyU996",

        // apiKey
        val apiKey: String =  "4f36a761c82acce48bc4c6c9cc30d68923fe92b5",

        // 货币
        val currency: String = "MYR",

        // 银行code
        val bankCode: String = "10000628",

        // 服务器请求ip
        val clientIp: String = "94.237.64.70",

        // 支付通知地址
        val backendURL: String  = "https://open.gpgaming88.com/api/v1/player/pay/surepay"

): PayConfig
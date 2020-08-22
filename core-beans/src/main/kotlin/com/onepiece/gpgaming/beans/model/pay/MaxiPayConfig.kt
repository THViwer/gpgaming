package com.onepiece.gpgaming.beans.model.pay

import com.onepiece.gpgaming.beans.enums.Bank

data class MaxiPayConfig (

        // api地址
        val apiPath: String = "https://gppay.gpgming88.com",

        // 商户id
        val merchantId: String = "gp",

        // apiKey
        val apiKey: String =  "4f36a761c82acce48bc4c6c9cc30d68923fe92b5",

        // 支持的银行列表
        val supportBanks:  List<Bank>,

        // 支付通知地址
        val backendURL: String  = "https://open.gpgaming88.com/api/v1/player/pay/gppay"

): PayConfig
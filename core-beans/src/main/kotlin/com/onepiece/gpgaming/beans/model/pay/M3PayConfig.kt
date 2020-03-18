package com.onepiece.gpgaming.beans.model.pay

class M3PayConfig(

        val apiPath: String = "http://payment.m3pay.com/epayment/entry.aspx",

        val merchant: String = "merchant.m3pay.com",

        val id: String = "T004",

        val memberCode: String = "T004",

        val pwd: String = "20200316_T004",

        val merchantKey: String = "dbb06d88-38ae-4aa5-a49a-2f77ce31efd8"

) : PayConfig

package com.onepiece.gpgaming.beans.model.token

data class AsiaGamingClientToken(

        val agentCode: String = "GB8_AGIN",

        val orderAgentCode: String = "GB8",

        val md5Secret: String = "B5K8dECZKMd3",

        val desSecret: String = "bHBXzrqj",

        val orderMd5Secret: String = "AF3A153DD4B398B082B24406C56D51E9",

        val currency: String = "MYR",

        val apiDomain: String = "https://gi.gpgaming88.com",

        val startGameApiDomain: String = "https://gci.gpgaming88.com"
) : ClientToken
package com.onepiece.gpgaming.beans

object SystemConstant {

    private const val AWS_CLIENT_URL = "https://s3.ap-southeast-1.amazonaws.com"

    private const val AWS_URL = "https://s3.ap-southeast-1.amazonaws.com/awspg1"

    const val AWS_LOGO_URL = "$AWS_URL/logo"

    const val AWS_ORIGIN_LOGO_URL = "$AWS_URL/origin_logo"

    const val AWS_BANK_LOGO_URL = "$AWS_URL/bank/logo"

    const val AWS_BANK_PROOF = "$AWS_URL/bank_proof"

    const val AWS_BANNER = "$AWS_URL/banner"

    const val AWS_PROMOTION = "$AWS_URL/promotion"

    const val AWS_SLOT = "$AWS_URL/slot"


    fun getClientResourcePath(clientId: Int, profile: String = "dev", defaultPath: String = ""): String {
        val bucktName = if (profile == "dev") "awspg1" else "awspg2"
        return "$AWS_CLIENT_URL/$bucktName$defaultPath"
    }

}
package com.onepiece.treasure.beans.enums

import com.onepiece.treasure.beans.SystemConstant

enum class Bank(
        val cname: String,
        val logo: String
) {

    BSN("BSN", "${SystemConstant.AWS_BANK_LOGO_URL}/BSN.jpeg"),

    CIMB("CIMB Bank", "${SystemConstant.AWS_BANK_LOGO_URL}/CIMB.jpeg"),

    HongLeong("HongLeong Bank", "${SystemConstant.AWS_BANK_LOGO_URL}/HongLeong.jpeg"),

    May("MayBank", "${SystemConstant.AWS_BANK_LOGO_URL}/May.jpeg"),

    PUBLIC("PUBLIC BANK", "${SystemConstant.AWS_BANK_LOGO_URL}/PUBLIC.jpeg"),

    RHB("RHB BANK", "${SystemConstant.AWS_BANK_LOGO_URL}/RHB.jpeg")


}
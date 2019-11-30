package com.onepiece.treasure.beans.enums

import com.onepiece.treasure.beans.SystemConstant

enum class Bank(
        val cname: String,
        val logo: String
) {

    BSN("BSN", "${SystemConstant.AWS_BANK_LOGO_URL}/BSN.jpeg"),

    CIMB("CIMB Bank", "${SystemConstant.AWS_BANK_LOGO_URL}/CIMB.png"),

    HongLeong("HongLeong Bank", "${SystemConstant.AWS_BANK_LOGO_URL}/HongLeong.png"),

    May("MayBank", "${SystemConstant.AWS_BANK_LOGO_URL}/May.png"),

    PUBLIC("PUBLIC BANK", "${SystemConstant.AWS_BANK_LOGO_URL}/PUBLIC.png"),

    RHB("RHB BANK", "${SystemConstant.AWS_BANK_LOGO_URL}/RHB.png")


}
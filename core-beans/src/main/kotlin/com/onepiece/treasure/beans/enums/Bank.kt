package com.onepiece.treasure.beans.enums

import com.onepiece.treasure.beans.SystemConstant

enum class Bank(
        val cname: String,
        val logo: String
) {

    BSN("BSN", "${SystemConstant.AWS_BANK_LOGO_URL}/logo/BSN.jpeg"),

    CIMB("CIMB Bank", "${SystemConstant.AWS_BANK_LOGO_URL}/logo/CIMB.png"),

    HongLeong("HongLeong Bank", "${SystemConstant.AWS_BANK_LOGO_URL}/logo/HongLeong.png"),

    May("MayBank", "${SystemConstant.AWS_BANK_LOGO_URL}/logo/May.png"),

    PUBLIC("PUBLIC BANK", "${SystemConstant.AWS_BANK_LOGO_URL}/logo/PUBLIC.png"),

    RHB("RHB BANK", "${SystemConstant.AWS_BANK_LOGO_URL}/logo/RHB.png")


}
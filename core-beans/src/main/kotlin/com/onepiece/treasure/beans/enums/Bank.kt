package com.onepiece.treasure.beans.enums

enum class Bank(
        val cname: String,
        val logo: String
) {

    BSN("BSN", "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/BSN.jpeg"),

    CIMB("CIMB Bank", "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/CIMB.png"),

    HongLeong("HongLeong Bank", "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/HongLeong.png"),

    May("MayBank", "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/May.png"),

    PUBLIC("PUBLIC BANK", "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/PUBLIC.png"),

    RHB("RHB BANK", "https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/RHB.png")


}
package com.onepiece.gpgaming.beans.enums

enum class Country(
        val logo: String
) {

    // 默认 总业主
    Default("https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/Malaysia.png"),

    // 新加坡
    Singapore("https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/Singapore.png"),

    // 马来西亚
    Malaysia("https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/Malaysia.png"),

    // 泰国
    Thailand("https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/Thailand.png"),

    // 越南
    Vietnam("https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/Vietnam.png"),

    // 印尼
    Indonesia("https://s3.ap-southeast-1.amazonaws.com/awspg1/bank/logo/Indonesia.png")

}
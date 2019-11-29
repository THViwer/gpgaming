package com.onepiece.treasure.beans.enums

enum class Bank(
        val cname: String,
        val logo: String
) {

    ICBC("中国工商银行", "https://s3.ap-southeast-1.amazonaws.com/awspg1/logo/joker.png"),

    ABC("中国农业银行", "https://s3.ap-southeast-1.amazonaws.com/awspg1/logo/joker.png")

}
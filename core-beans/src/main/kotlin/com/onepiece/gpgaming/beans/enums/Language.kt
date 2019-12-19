package com.onepiece.gpgaming.beans.enums

/**
 * 支持的语言列表
 */
enum class Language(
        val ename: String,
        val cname: String
) {

    // 英语
    EN("English", "英语"),

    // 中文
    CN("Chinese", "中文"),

    // 马来语
    MY("Malay", "马来西亚"),

    // 印尼
    ID("Indonesian", "印度尼西亚"),

    // 泰国
    TH("Thailand", "泰语"),

    // 越南
    VI("Vietnamese", "越南语")

}
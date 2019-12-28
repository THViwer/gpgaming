package com.onepiece.gpgaming.beans.enums

enum class PromotionCategory(
        val ename: String,
        val cname: String
) {

    First("New Member", "新会员"),

    Special("special promotion", "特别优惠"),

    Slot("slot", "老虎机"),

    Live("live casino", "真人视频"),

    Sport("sport", "体育"),

    Fishing("fishing", "捕鱼")

}
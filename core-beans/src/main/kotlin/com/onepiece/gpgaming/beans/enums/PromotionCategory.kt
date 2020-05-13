package com.onepiece.gpgaming.beans.enums

enum class PromotionCategory(
        val ename: String,
        val cname: String
) {

    // 首充
    First("New Member", "首充"),

    // 特别优惠
    Special("special promotion", "特别优惠"),

    // 返水优惠
    Backwater("backwater", "返水"),

    // 其它
    Other("other", "其它"),


    // 用作显示的优惠
    Slot("slot", "老虎机"),

    Live("live casino", "真人视频"),

    Sport("sport", "体育"),

    Fishing("fishing", "捕鱼")


}
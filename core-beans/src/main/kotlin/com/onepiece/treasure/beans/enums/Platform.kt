package com.onepiece.treasure.beans.enums

enum class Platform(
        val category: PlatformCategory,
        val cname: String,
        val icon: String
) {

    // 中心 用于钱包 不用于游戏
    Center(PlatformCategory.Slot, "center", ""),

    // slot
    Joker(PlatformCategory.Slot, "joker", "https://ali88win.com/img/product-logo/joker.png"),
    Kiss918(PlatformCategory.Slot, "918kiss", "https://ali88win.com/img/product-logo/joker.png"),

    // live video
    Cta666(PlatformCategory.LiveVideo, "cta666", "https://www.bk8my.com/public/new_bk8/content/images/firms/firms_mobile_mega_888_of.png")

    ;

    companion object {

        fun all(): List<Platform> {
            return values().filter { it != Center }
        }

    }

}
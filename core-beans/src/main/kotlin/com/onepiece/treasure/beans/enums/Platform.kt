package com.onepiece.treasure.beans.enums

enum class Platform(

        // 平台类型
        val category: PlatformCategory,

        // 平台名称
        val cname: String,

        // 图标
        val icon: String,

        // 启动平台列表
        val starts: List<StartPlatform>
) {

    // 中心 用于钱包 不用于游戏
    Center(PlatformCategory.Slot, "center", "", listOf()),

    // slot
    Joker(
            PlatformCategory.Slot,
            "joker",
            "https://ali88win.com/img/product-logo/joker.png",
            listOf(StartPlatform.Pc, StartPlatform.Wap)),
    Kiss918(
            PlatformCategory.Slot,
            "918kiss",
            "https://ali88win.com/img/product-logo/joker.png",
            listOf(StartPlatform.Pc, StartPlatform.Wap, StartPlatform.Android)),

    Pussy888(
            PlatformCategory.Slot,
            "pussy888",
            "https://ali88win.com/img/product-logo/joker.png",
            listOf(StartPlatform.Pc, StartPlatform.Wap, StartPlatform.Android)),

    Mega(
            PlatformCategory.Slot,
            "mega",
            "https://ali88win.com/img/product-logo/joker.png",
            listOf(StartPlatform.Ios, StartPlatform.Android)),

    // live video
    CT(
            PlatformCategory.LiveVideo,
            "ct",
            "https://www.bk8my.com/public/new_bk8/content/images/firms/firms_mobile_mega_888_of.png",
            listOf(StartPlatform.Ios, StartPlatform.Android)),

    DG(
            PlatformCategory.LiveVideo,
            "dg",
            "https://www.bk8my.com/public/new_bk8/content/images/firms/firms_mobile_mega_888_of.png",
            listOf(StartPlatform.Ios, StartPlatform.Android)),

    Evolution(
            PlatformCategory.LiveVideo,
            "evolution",
            "https://www.bk8my.com/public/new_bk8/content/images/firms/firms_mobile_mega_888_of.png",
            listOf(StartPlatform.Pc, StartPlatform.Wap)
    ),

    GoldDeluxe(
            PlatformCategory.LiveVideo,
            "golddeluxe",
            "https://www.bk8my.com/public/new_bk8/content/images/firms/firms_mobile_mega_888_of.png",
            listOf(StartPlatform.Pc, StartPlatform.Wap)
    ),

    // sport
    Sbo(
            PlatformCategory.Sport,
            "sbo",
            "https://www.bk8my.com/public/new_bk8/content/images/firms/firms_mobile_mega_888_of.png",
            listOf(StartPlatform.Pc, StartPlatform.Wap))

    ;

    companion object {

        fun all(): List<Platform> {
            return values().filter { it != Center }
        }

    }

}
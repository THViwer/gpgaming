package com.onepiece.treasure.beans.enums

enum class Platform(
        val category: PlatformCategory,
        val cname: String
) {

    // 中心 用于钱包 不用于游戏
    Center(PlatformCategory.Slot, "center"),

    Joker(PlatformCategory.Slot, "joker"),

    Cta666(PlatformCategory.LiveVideo, "cta666")

    ;

    companion object {

        fun all(): List<Platform> {
            return values().filter { it != Center }
        }

    }

}
package com.onepiece.treasure.beans.enums

enum class Platform(
        val category: PlatformCategory,
        val cname: String
) {

    // 中心 用于钱包 不用于游戏
    Center(PlatformCategory.Slot, "center"),

    AG(PlatformCategory.Slot, "ag"),

    SUN(PlatformCategory.Slot, "sun")
    ;

    companion object {

        fun all(): List<Platform> {
            return values().filter { it != Center }
        }

    }

}
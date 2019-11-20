package com.onepiece.treasure.beans.enums


enum class Platform(
        val detail: PlatformDetail
) {

    // 中心 用于钱包 不用于游戏
    Center(PlatformDetail.ofCenter()),

    // slot
    Joker(PlatformDetail.ofJoker()),
    Kiss918(PlatformDetail.ofKiss918()),
    Pussy888(PlatformDetail.ofPussy888()),
    Mega(PlatformDetail.ofMega()),

    // live video
//    CT(PlatformDetail.ofCT()),
//    DG(PlatformDetail.ofDG()),
    Evolution(PlatformDetail.ofEvolution()),
    GoldDeluxe(PlatformDetail.ofGoldDeluxe()),
    SexyGaming(PlatformDetail.ofSexyGaming()),
    Fgg(PlatformDetail.ofFgg()),

    // sport
    Sbo(PlatformDetail.ofSbo()),
    Lbc(PlatformDetail.ofLbc()),
    Bcs(PlatformDetail.ofBcs())

    ;

    companion object {

        fun all(): List<Platform> {
            return values().filter { it != Center }
        }
    }

}

open class PlatformDetail private constructor(

        // 平台类型
        val category: PlatformCategory,

        // 平台名称
        val name: String,

        // 图标
        val icon: String,

        // 平台维护图标
        val disableIcon: String,

        // 状态
        val status: Status,

        // 启动方式
        val launchs: List<LaunchMethod> = listOf(LaunchMethod.Web, LaunchMethod.Wap)
) {

    companion object {

        fun ofCenter(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "AMZBET", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal, launchs = emptyList())
        }

        // slot
        fun ofJoker(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "Joker", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal, launchs = listOf(LaunchMethod.Web, LaunchMethod.Wap, LaunchMethod.Android))
        }

        fun ofKiss918(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "Kiss918", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal, launchs = listOf(LaunchMethod.Ios, LaunchMethod.Android))
        }

        fun ofPussy888(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "Pussy888", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal, launchs = listOf(LaunchMethod.Ios, LaunchMethod.Android))
        }

        fun ofMega(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "Mega", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal, launchs = listOf(LaunchMethod.Ios, LaunchMethod.Android))
        }

        // live game
        fun ofCT(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.LiveVideo, name = "CT", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal)
        }

        fun ofDG(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.LiveVideo, name = "GD", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal)
        }

        fun ofEvolution(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.LiveVideo, name = "Evolution", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal)
        }

        fun ofGoldDeluxe(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.LiveVideo, name = "GoldDeluxe", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal)
        }
        fun ofSexyGaming(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.LiveVideo, name = "sexy gaming", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal)
        }

        fun ofFgg(): PlatformDetail {

            return PlatformDetail(category = PlatformCategory.LiveVideo, name = "fgg", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal)

        }

        // soprt
        fun ofSbo(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Sport, name = "sbo", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal)
        }

        fun ofLbc(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Sport, name = "lbc", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal)
        }

        fun ofBcs(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Sport, name = "BCS", icon = "https://ali88win.com/img/product-logo/joker.png",
                    disableIcon = "https://ali88win.com/img/product-logo/joker.png", status = Status.Normal)
        }

    }


}


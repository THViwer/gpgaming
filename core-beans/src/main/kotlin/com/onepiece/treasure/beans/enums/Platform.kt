package com.onepiece.treasure.beans.enums

import com.onepiece.treasure.beans.SystemConstant


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
    Pragmatic(PlatformDetail.ofPragmatic()),
    SpadeGaming(PlatformDetail.ofSpadeGaming()),
    TTG(PlatformDetail.ofTTG()),
    MicroGaming(PlatformDetail.ofMicroGaming()),

    // live video
    CT(PlatformDetail.ofCT()),
    DreamGaming(PlatformDetail.ofDreamGaming()),
    Evolution(PlatformDetail.ofEvolution()),
    GoldDeluxe(PlatformDetail.ofGoldDeluxe()),
    SexyGaming(PlatformDetail.ofSexyGaming()),
    Fgg(PlatformDetail.ofFgg()),
    AllBet(PlatformDetail.ofAllBet()),

    // sport
//    Sbo(PlatformDetail.ofSbo()),
    Lbc(PlatformDetail.ofLbc()),
    Bcs(PlatformDetail.ofBcs()),
    CMD(PlatformDetail.ofCMD()),

    // fishing
    GGFishing(PlatformDetail.ofGGFishng())

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

        private const val defaultLogoPath = "${SystemConstant.AWS_LOGO_URL}/joker.png"

        fun ofCenter(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "AMZBET", icon = defaultLogoPath,
                    disableIcon = defaultLogoPath, status = Status.Normal, launchs = emptyList())
        }

        // slot
        fun ofJoker(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "Joker", icon = "${SystemConstant.AWS_LOGO_URL}/joker.png",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}joker.png", status = Status.Normal, launchs = listOf(LaunchMethod.Web, LaunchMethod.Wap, LaunchMethod.Android))
        }
        fun ofKiss918(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "918kiss", icon = "${SystemConstant.AWS_LOGO_URL}/918kiss.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/918kiss.jpeg", status = Status.Normal, launchs = listOf(LaunchMethod.Ios, LaunchMethod.Android))
        }
        fun ofPussy888(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "Pussy888", icon = "${SystemConstant.AWS_LOGO_URL}/pussy.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/pussy.jpeg", status = Status.Normal, launchs = listOf(LaunchMethod.Ios, LaunchMethod.Android))
        }
        fun ofMega(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "Mega", icon = "${SystemConstant.AWS_LOGO_URL}/mega.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/mega.jpeg", status = Status.Normal, launchs = listOf(LaunchMethod.Ios, LaunchMethod.Android))
        }
        fun ofPragmatic(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "pragmatic", icon = "${SystemConstant.AWS_LOGO_URL}/pragmatic.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/pragmatic.jpeg", status = Status.Normal)
        }
        fun ofSpadeGaming(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "spade gaming", icon = "${SystemConstant.AWS_LOGO_URL}/spadeGaming.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/spadeGaming.jpeg", status = Status.Normal)
        }
        fun ofTTG(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "top trend gamimg", icon = "${SystemConstant.AWS_LOGO_URL}/spadeGaming.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/spadeGaming.jpeg", status = Status.Normal)
        }
        fun ofMicroGaming(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Slot, name = "Micro Gaming", icon = "${SystemConstant.AWS_LOGO_URL}/microGaming.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/microGaming.jpeg", status = Status.Normal)
        }

        // live game
        fun ofCT(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.LiveVideo, name = "CT", icon = defaultLogoPath,
                    disableIcon = defaultLogoPath, status = Status.Stop)
        }

        fun ofDreamGaming(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.LiveVideo, name = "DreamGaming", icon = "${SystemConstant.AWS_LOGO_URL}/dreamGaming.jpeg",
                    disableIcon = "{SystemConstant.AWS_LOGO_URL}/dreamGaming.jpeg", status = Status.Normal)
        }

        fun ofEvolution(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.LiveVideo, name = "Evolution", icon = "${SystemConstant.AWS_LOGO_URL}/evolution.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/evolution.jpeg", status = Status.Normal)
        }

        fun ofGoldDeluxe(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.LiveVideo, name = "GoldDeluxe", icon = "${SystemConstant.AWS_LOGO_URL}/goldGeluxe.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/goldGeluxe.jpeg", status = Status.Normal)
        }
        fun ofSexyGaming(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.LiveVideo, name = "sexy gaming", icon = "${SystemConstant.AWS_LOGO_URL}/saGaming.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/saGaming.jpeg", status = Status.Normal)
        }

        fun ofFgg(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.LiveVideo, name = "fgg", icon = defaultLogoPath,
                    disableIcon = defaultLogoPath, status = Status.Normal)
        }

        fun ofAllBet(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.LiveVideo, name = "allbet", icon = "${SystemConstant.AWS_LOGO_URL}/allBet.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/allBet.jpeg", status = Status.Normal)
        }

        // soprt
        fun ofSbo(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Sport, name = "sbo", icon = defaultLogoPath,
                    disableIcon = defaultLogoPath, status = Status.Stop)
        }

        fun ofLbc(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Sport, name = "lbc", icon = "${SystemConstant.AWS_LOGO_URL}/lbc.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/lbc.jpeg", status = Status.Normal)
        }

        fun ofBcs(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Sport, name = "BCS", icon = defaultLogoPath,
                    disableIcon = defaultLogoPath, status = Status.Normal)
        }
        fun ofCMD(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Sport, name = "CMD", icon = "${SystemConstant.AWS_LOGO_URL}/cmd.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/cmd.jpeg", status = Status.Normal)
        }
        // Fishing
        fun ofGGFishng(): PlatformDetail {
            return PlatformDetail(category = PlatformCategory.Fishing, name = "GGFishing", icon = "${SystemConstant.AWS_LOGO_URL}/GGFishing.jpeg",
                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/GGFishing.jpeg", status = Status.Normal)
        }

    }


}


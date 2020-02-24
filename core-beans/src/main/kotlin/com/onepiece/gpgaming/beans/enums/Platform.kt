package com.onepiece.gpgaming.beans.enums

import com.onepiece.gpgaming.beans.model.GamePlatform


enum class Platform(
        val category: PlatformCategory,
        val hotGameLogo: String? = null
){

    // 中心 用于钱包 不用于游戏
    Center(PlatformCategory.Slot),

    // slot
    Joker(PlatformCategory.Slot),
    Kiss918(PlatformCategory.Slot),
    Pussy888(PlatformCategory.Slot, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/pussy888.png"),
    Mega(PlatformCategory.Slot, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/mega888.png"),
    Pragmatic(PlatformCategory.Slot, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/pragmatic.png"),
    SpadeGaming(PlatformCategory.Slot, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/spadegaming.png"),
    TTG(PlatformCategory.Slot, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/top.png"),
    MicroGaming(PlatformCategory.Slot, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/microgaming.png"),
    PlaytechSlot(PlatformCategory.Slot, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/playtech.png"),
    PNG(PlatformCategory.Slot, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/playgo.png"),
    GamePlay(PlatformCategory.Slot, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/playgame.png"),
    SimplePlay(PlatformCategory.Slot, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/sagaming.png"), // 就是sa 的slot
    AsiaGamingSlot(PlatformCategory.Slot, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/asiagaming.png"),

    // live video
    CT(PlatformCategory.LiveVideo),
    DreamGaming(PlatformCategory.LiveVideo),
    Evolution(PlatformCategory.LiveVideo),
    GoldDeluxe(PlatformCategory.LiveVideo),
    SexyGaming(PlatformCategory.LiveVideo),
    Fgg(PlatformCategory.LiveVideo),
    AllBet(PlatformCategory.LiveVideo),
    SaGaming(PlatformCategory.LiveVideo, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/sagaming.png"),
    AsiaGamingLive(PlatformCategory.LiveVideo, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/asiagaming.png"),
    MicroGamingLive(PlatformCategory.LiveVideo, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/microgaming.png"),
    PlaytechLive(PlatformCategory.LiveVideo, "https://s3.ap-southeast-1.amazonaws.com/awspg1/hotGame/logo/playtech.png"),

    // sport
//    Sbo(PlatformDetail.ofSbo()),
    Lbc(PlatformCategory.Sport),
    Bcs(PlatformCategory.Sport),
    CMD(PlatformCategory.Sport),

    // fishing
    GGFishing(PlatformCategory.Fishing)

    ;

    companion object {
        fun all(): List<Platform> {
            return values().filter { it != Center && it != CT }
        }
    }

    fun getGamePlatform(gamePlatforms: List<GamePlatform>): GamePlatform {
        return gamePlatforms.first { it.platform == this }
    }

}
//
//open class PlatformDetail private constructor(
//
//        // 平台类型
//        val category: PlatformCategory,
//
//        // 平台名称
//        val name: String,
//
//        // 图标
//        val icon: String,
//
//        // 平台维护图标
//        val disableIcon: String?,
//
//        // 原始图标
//        val originIcon: String,
//
//        // 原始鼠标移上去图标
//        val originIconOver: String,
//
//        // 是否有试玩
//        val demo: Boolean = false,
//
//        // 状态
//        val status: Status,
//
//        // 启动方式
//        val launchs: List<LaunchMethod> = listOf(LaunchMethod.Web, LaunchMethod.Wap)
//) {
//
//    companion object {
//
//        private const val defaultLogoPath = "${SystemConstant.AWS_LOGO_URL}/joker.jpg"
//
//        fun ofCenter(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Slot,
//                    name = "AMZBET",
//                    icon = defaultLogoPath,
//                    disableIcon = defaultLogoPath,
//                    status = Status.Normal,
//                    launchs = emptyList(),
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//
//        // slot
//        fun ofJoker(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Slot,
//                    name = "Joker",
//                    demo = true,
//                    status = Status.Normal,
//                    launchs = listOf(LaunchMethod.Web, LaunchMethod.Wap, LaunchMethod.Android),
//                    icon = "${SystemConstant.AWS_LOGO_URL}/joker.jpg",
//                    disableIcon = null,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofKiss918(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Slot,
//                    name = "918kiss",
//                    status = Status.Normal,
//                    launchs = listOf(LaunchMethod.Ios, LaunchMethod.Android),
//                    icon = "${SystemConstant.AWS_LOGO_URL}/918kiss.jpg",
//                    disableIcon = null,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofPussy888(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Slot,
//                    name = "Pussy888",
//                    status = Status.Normal,
//                    launchs = listOf(LaunchMethod.Ios, LaunchMethod.Android),
//                    icon = "${SystemConstant.AWS_LOGO_URL}/pussy888.jpg",
//                    disableIcon = null,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofMega(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Slot,
//                    name = "Mega",
//                    status = Status.Normal,
//                    launchs = listOf(LaunchMethod.Ios, LaunchMethod.Android),
//                    icon = "${SystemConstant.AWS_LOGO_URL}/mega888.jpg",
//                    disableIcon = null,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofPragmatic(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Slot,
//                    name = "pragmatic",
//                    status = Status.Normal,
//                    demo = true,
//                    icon = "${SystemConstant.AWS_LOGO_URL}/pragmatic.jpg",
//                    disableIcon = null,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofSpadeGaming(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Slot,
//                    name = "spade gaming",
//                    status = Status.Normal,
//                    demo = true,
//                    icon = "${SystemConstant.AWS_LOGO_URL}/spadegaming.jpg",
//                    disableIcon = null,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofTTG(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Slot,
//                    name = "top trend gamimg",
//                    status = Status.Normal,
//                    demo = true,
//                    icon = "${SystemConstant.AWS_LOGO_URL}/toprend.jpg",
//                    disableIcon = null,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofMicroGaming(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Slot,
//                    name = "Micro Gaming",
//                    status = Status.Normal,
//                    demo = true,
//                    icon = "${SystemConstant.AWS_LOGO_URL}/microgaming.jpg",
//                    disableIcon = null,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofPlaytechSlot(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Slot,
//                    name = "playtech",
//                    status = Status.Normal,
//                    demo = false,
//                    icon = "${SystemConstant.AWS_LOGO_URL}/playtech.jpg",
//                    disableIcon = null,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofPNG(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Slot,
//                    name = "play n go",
//                    status = Status.Normal,
//                    demo = true,
//                    icon = "${SystemConstant.AWS_LOGO_URL}/play-n-go.jpg",
//                    disableIcon = null,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofGamePlay(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Slot,
//                    name = "game play",
//                    status = Status.Normal,
//                    demo = true,
//                    icon = "${SystemConstant.AWS_LOGO_URL}/gameplay.jpg",
//                    disableIcon = null,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofSimplePlay(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Slot,
//                    name = "simple play",
//                    icon = "${SystemConstant.AWS_LOGO_URL}/asia-gaming-slot.jpg",
//                    disableIcon = null,
//                    status = Status.Normal,
//                    demo = true,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//
//        // live game
//        fun ofCT(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.LiveVideo,
//                    name = "CT",
//                    icon = defaultLogoPath,
//                    disableIcon = defaultLogoPath,
//                    status = Status.Delete,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//
//        fun ofDreamGaming(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.LiveVideo,
//                    name = "DreamGaming",
//                    icon = "${SystemConstant.AWS_LOGO_URL}/dream-gaming.jpg",
//                    disableIcon = "{SystemConstant.AWS_LOGO_URL}/dreamGaming.jpeg",
//                    status = Status.Normal,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//
//        fun ofEvolution(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.LiveVideo,
//                    name = "Evolution",
//                    icon = "${SystemConstant.AWS_LOGO_URL}/evolution.jpg",
//                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/evolution.jpeg",
//                    status = Status.Normal,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//
//        fun ofGoldDeluxe(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.LiveVideo,
//                    name = "GoldDeluxe",
//                    icon = "${SystemConstant.AWS_LOGO_URL}/gold-deluxe.jpg",
//                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/goldGeluxe.jpeg",
//                    status = Status.Normal,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofSexyGaming(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.LiveVideo,
//                    name = "sexy gaming",
//                    icon = "${SystemConstant.AWS_LOGO_URL}/sexy-gaming.jpg",
//                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/saGaming.jpeg",
//                    status = Status.Normal,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//
//        fun ofFgg(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.LiveVideo,
//                    name = "fgg",
//                    icon = "${SystemConstant.AWS_LOGO_URL}/guaranted.jpg",
//                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/fgg.png",
//                    status = Status.Normal,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//
//        fun ofAllBet(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.LiveVideo,
//                    name = "allbet",
//                    icon = "${SystemConstant.AWS_LOGO_URL}/allbet.jpg",
//                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/allBet.jpeg",
//                    status = Status.Normal,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofSaGaming(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.LiveVideo,
//                    name = "sa gaming",
//                    icon = "${SystemConstant.AWS_LOGO_URL}/sa-gaming.jpg",
//                    disableIcon = "${SystemConstant.AWS_LOGO_URL}/saGaming.jpeg",
//                    status = Status.Normal,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofAsiaGaming(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.LiveVideo,
//                    name = "asia gaming",
//                    icon = "${SystemConstant.AWS_LOGO_URL}/asia-gaming-live.jpg",
//                    disableIcon = null,
//                    status = Status.Normal,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//
//        fun ofMicroGamingLive(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.LiveVideo,
//                    name = "Micro Gaming",
//                    status = Status.Normal,
//                    demo = true,
//                    icon = "${SystemConstant.AWS_LOGO_URL}/micro-gaming-live.jpg",
//                    disableIcon = null,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofPlaytechLive(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.LiveVideo,
//                    name = "playtech",
//                    status = Status.Normal,
//                    demo = false,
//                    icon = "${SystemConstant.AWS_LOGO_URL}/playtech.jpg",
//                    disableIcon = null,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//
//
//        // soprt
//        fun ofSbo(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Sport,
//                    name = "sbo",
//                    icon = defaultLogoPath,
//                    disableIcon = defaultLogoPath,
//                    status = Status.Stop,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//
//        fun ofLbc(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Sport,
//                    name = "ibc",
//                    icon = "${SystemConstant.AWS_LOGO_URL}/maxbet.jpg",
//                    disableIcon = null,
//                    status = Status.Normal,
//                    demo = true,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//
//        fun ofBcs(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Sport,
//                    name = "amzbet",
//                    icon = "${SystemConstant.AWS_LOGO_URL}/amzbet.jpg",
//                    disableIcon = null,
//                    status = Status.Normal,
//                    demo = true,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        fun ofCMD(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Sport,
//                    name = "CMD",
//                    icon = "${SystemConstant.AWS_LOGO_URL}/amd.jpg",
//                    disableIcon = null,
//                    status = Status.Normal,
//                    demo = true,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//        // Fishing
//        fun ofGGFishng(): PlatformDetail {
//            return PlatformDetail(
//                    category = PlatformCategory.Fishing,
//                    name = "GGFishing",
//                    icon = "${SystemConstant.AWS_LOGO_URL}/GGFishing.jpg",
//                    disableIcon = null,
//                    status = Status.Normal,
//                    originIcon = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa.png",
//                    originIconOver = "${SystemConstant.AWS_ORIGIN_LOGO_URL}/firms_slot_logo_sa_over.png")
//        }
//
//    }
//
//
//}


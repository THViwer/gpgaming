package com.onepiece.treasure.controller.value

import com.onepiece.treasure.beans.enums.*
import com.onepiece.treasure.beans.value.internet.web.SlotCategory
import io.swagger.annotations.ApiModelProperty


data class ConfigVo(

        @ApiModelProperty("公告")
        val announcementVo: AnnouncementVo?,

        @ApiModelProperty("平台列表")
        val platforms: List<PlatformVo>,

        @ApiModelProperty("banner列表")
        val banners: List<BannerVo>,

        @ApiModelProperty("热门游戏")
        val hotGameUrl: String

)

data class PlatformCategoryPage(

        @ApiModelProperty("平台列表")
        val platforms: List<Platform>,

        @ApiModelProperty("banner")
        val banners: List<BannerVo>

)

data class AnnouncementVo(

        @ApiModelProperty("标题")
        val title: String,

        @ApiModelProperty("简介")
        val synopsis: String?,

        @ApiModelProperty("内容")
        val content: String

//        @ApiModelProperty("创建时间")
//        val createdTime: LocalDateTime
)

data class BannerVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("排序")
        val order: Int,

        @ApiModelProperty("图标")
        val icon: String,

        @ApiModelProperty("鼠标移动上去图标")
        val touchIcon: String?,

        @ApiModelProperty("类型")
        val type: BannerType,

        @ApiModelProperty("链接地址")
        val link: String?
)
//
//data class HotGameVo(
//
//        @ApiModelProperty("图标")
//        val icon: String,
//
//        @ApiModelProperty("平台")
//        val platform: Platform,
//
//        @ApiModelProperty("游戏Id")
//        val gameId: String,
//
//        @ApiModelProperty("是否热门")
//        val hot: Boolean,
//
//        @ApiModelProperty("是否新游戏")
//        val new: Boolean
//) {
//
//        companion object {
//
//                fun of(): List<HotGameVo> {
//
//
//                        val greekGods = HotGameVo(platform = Platform.Pragmatic, icon = "https://api.prerelease-env.biz/game_pic/rec/325/vs243fortseren.png", gameId = "vs243fortseren", hot = true, new = false)
//
//
//
//
//                        val icon = "https://www.bk8my.com/public/new_bk8/content/images/hotgame_buffalo_blitz_1.jpg"
//                        val h1 = HotGameVo(icon = icon, platform = Platform.Pussy888, gameId = StringUtil.generateNonce(5), hot = true, new = false)
//                        val h2 = HotGameVo(icon = icon, platform = Platform.Kiss918, gameId = StringUtil.generateNonce(5), hot = false, new = false)
//                        val h3 = HotGameVo(icon = icon, platform = Platform.Joker, gameId = StringUtil.generateNonce(5), hot = true, new = false)
//                        val h4 = HotGameVo(icon = icon, platform = Platform.Joker, gameId = StringUtil.generateNonce(5), hot = false, new = false)
//                        val h5 = HotGameVo(icon = icon, platform = Platform.Joker, gameId = StringUtil.generateNonce(5), hot = false, new = false)
//                        val h6 = HotGameVo(icon = icon, platform = Platform.Mega, gameId = StringUtil.generateNonce(5), hot = false, new = true)
//                        val h7 = HotGameVo(icon = icon, platform = Platform.Mega, gameId = StringUtil.generateNonce(5), hot = true, new = false)
//                        val h8 = HotGameVo(icon = icon, platform = Platform.Pussy888, gameId = StringUtil.generateNonce(5), hot = true, new = false)
//                        val h9 = HotGameVo(icon = icon, platform = Platform.Pussy888, gameId = StringUtil.generateNonce(5), hot = false, new = true)
//                        val h10 = HotGameVo(icon = icon, platform = Platform.Kiss918, gameId = StringUtil.generateNonce(5), hot = true, new = false)
//
//                        return listOf(h1, h2, h3, h4, h5, h6, h7, h8, h9, h10)
//                }
//        }
//}



data class PlatformVo(

        @ApiModelProperty("平台Id")
        val id: Int,

        @ApiModelProperty("平台")
        val platform: Platform,

        @ApiModelProperty("名称")
        val name: String,

        @ApiModelProperty("图标")
        val icon: String,

        @ApiModelProperty("类目")
        val category: PlatformCategory,

        @ApiModelProperty("是否有试玩")
        val demo: Boolean,

        @ApiModelProperty("平台状态")
        val status: Status,

        @ApiModelProperty("支持启动平台")
        val launchs: List<LaunchMethod>
)

data class StartGameResp(

        @ApiModelProperty("平台地址")
        val path: String
)

data class DownloadAppVo(

        @ApiModelProperty("平台")
        val platform: Platform,

        @ApiModelProperty("图标")
        val icon: String,

        @ApiModelProperty("ios下载地址")
        val iosPath: String?,

        @ApiModelProperty("android下载地址")
        val androidPath: String?
)

data class PlatformMembrerDetail(
        val username: String,

        val password: String
)

data class PlatformCategoryDetail(

        @ApiModelProperty("平台列表")
        val platforms: List<PlatformVo>,

        @ApiModelProperty("banners")
        val banners: List<BannerVo>,

        @ApiModelProperty("游戏列表(只有在slot下才有)")
        val games: List<SlotCategory>? = null

)
package com.onepiece.treasure.controller.value

import com.onepiece.treasure.beans.enums.*
import io.swagger.annotations.ApiModelProperty


data class ConfigVo(

        @ApiModelProperty("logo")
        val logo: String,

        @ApiModelProperty("公告")
        val announcementVo: AnnouncementVo?,

        @ApiModelProperty("平台列表")
        val platforms: List<PlatformVo>,

        @ApiModelProperty("banner列表")
        val banners: List<BannerVo>,

        @ApiModelProperty("热门游戏")
        val hotGameUrl: String,

        @ApiModelProperty("推荐平台列表")
        val recommendedPlatforms: List<RecommendedPlatform>,

        @ApiModelProperty("视频介绍")
        val lastestVideo: LastestVideo,

        @ApiModelProperty("捕鱼推荐")
        val fishes: List<FishingRecommended>,

        @ApiModelProperty("直人推荐")
        val lives: List<LiveRecommended>

) {

        data class RecommendedPlatform(

                @ApiModelProperty("类别")
                val category: PlatformCategory,

                @ApiModelProperty("平台")
                val platform: Platform,

                @ApiModelProperty("logo")
                val logo: String,

                @ApiModelProperty("鼠标移上去logo")
                val touchLogo: String

        )

        data class LastestVideo(
                @ApiModelProperty("地址")
                val path: String,

                @ApiModelProperty("介绍图片")
                val introductionImage: String
        )

        data class LiveRecommended(

                @ApiModelProperty("platform")
                val platform: Platform,

                @ApiModelProperty("logo")
                val originLogo: String,

                @ApiModelProperty("内容图片")
                val contentImage: String,

                @ApiModelProperty("标题")
                val title: String
        )

        data class FishingRecommended(

                @ApiModelProperty("平台")
                val platform: Platform,

                @ApiModelProperty("内容图片")
                val contentImage: String,

                @ApiModelProperty("内容")
                val content: String
        )




}

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
        val url: String?

//        @ApiModelProperty("游戏列表(只有在slot下才有)")
//        val games: List<SlotCategory>? = null

)
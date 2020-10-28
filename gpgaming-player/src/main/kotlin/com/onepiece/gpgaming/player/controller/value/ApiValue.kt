package com.onepiece.gpgaming.player.controller.value

import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Status
import io.swagger.annotations.ApiModelProperty

sealed class ApiValue {

        data class GuideConfigVo(

                @ApiModelProperty("logo")
                val logo: String,

                @ApiModelProperty("shortcutLogo")
                val shortcutLogo: String,

                @ApiModelProperty("主站的域名")
                val mainPath: String,

                @ApiModelProperty("国家列表")
                val countries: List<CountryVo>
        ) {
                data class CountryVo(

                        // 国家
                        val country: Country,

                        // 域名
                        val path: String,

                        // 是否是主站
                        val main: Boolean
                ) {

                        val logo: String = country.logo

                }

        }

}

data class IndexConfig(
        val url: String
)

data class HotGameVo(

        @ApiModelProperty("游戏名")
        val name: String,

        @ApiModelProperty("介绍")
        val introduce: String,

        @ApiModelProperty("平台")
        val platform: Platform,

        @ApiModelProperty("游戏Id")
        val gameId: String,

        @ApiModelProperty("logo")
        val logo: String?,

        @ApiModelProperty("图片1")
        val img1: String,

        @ApiModelProperty("图片2")
        val img2: String?,

        @ApiModelProperty("图片3")
        val img3: String?,

        @ApiModelProperty("是否试玩")
        val demo: Boolean = false
)

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

        @ApiModelProperty("标题")
        val title: String?,

        @ApiModelProperty("介绍")
        val introduce: String?,

        @ApiModelProperty("鼠标移动上去图标")
        val touchIcon: String?,

        @ApiModelProperty("类型")
        val type: BannerType,

        @ApiModelProperty("平台类目")
        val platformCategory: PlatformCategory?,

        @ApiModelProperty("链接地址")
        val link: String?
)

data class PlatformVo(

        @ApiModelProperty("平台Id")
        val id: Int,

        @ApiModelProperty("平台")
        val platform: Platform,

        @ApiModelProperty("是否热门")
        val hot: Boolean,

        @ApiModelProperty("是否新平台")
        val new: Boolean,

        @ApiModelProperty("名称")
        val name: String,

        @ApiModelProperty("图标")
        val icon: String,

        @ApiModelProperty("unclejay平台大图标")
        val unclejayMobileIcon: String?,

        @ApiModelProperty("原始图标")
        val originIcon: String,

        @ApiModelProperty("原始图标移动上去图杯")
        val originIconOver: String,

        @ApiModelProperty("类目下面详细图杯")
        val categoryDetailIcon: String? = "-",

        @ApiModelProperty("平台维护图标")
        val disableIcon: String?,

        // 平台详细图标
        val platformDetailIcon: String?,

        // 平台详情鼠标移动图片
        val platformDetailIconOver: String?,

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
        val path: String,

        @ApiModelProperty("用户名")
        val username: String = "-",

        @ApiModelProperty("密码")
        val password: String = "-",

        @ApiModelProperty("其它额外参数")
        val params: Map<String, String> = hashMapOf()
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
) {

        val pname: String
                @ApiModelProperty("平台名称")
                get() {
                        return platform.pname
                }

}

data class PlatformMembrerDetail(
        val username: String,

        val password: String
)

data class PlatformCategoryDetail(

        @ApiModelProperty("平台列表")
        val platforms: List<PlatformVo>,

        @ApiModelProperty("banners")
        val banners: List<BannerVo>

//        @ApiModelProperty("游戏列表(只有在slot下才有)")
//        val url: String?

//        @ApiModelProperty("游戏列表(只有在slot下才有)")
//        val games: List<SlotCategory>? = null

)
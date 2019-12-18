package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.PlatformCategory
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.I18nContent
import com.onepiece.treasure.beans.model.Recommended
import io.swagger.annotations.ApiModelProperty

data class Index(

        @ApiModelProperty("logo")
        val logo: String,

        @ApiModelProperty("公告")
        val announcement: I18nContent.AnnouncementI18n,

        @ApiModelProperty("平台列表")
        val platforms: List<PlatformVo>,

        @ApiModelProperty("banner列表")
        val banners: List<BannerVo>,

        @ApiModelProperty("热门游戏")
        val hotGameUrl: String,

        @ApiModelProperty("推荐平台列表")
        val recommendedPlatforms: List<Recommended.RecommendedPlatform>,

        @ApiModelProperty("视频介绍")
        val recommendedVideos: List<Recommended.VideoRecommended>,

        @ApiModelProperty("体育推荐")
        val sports: List<Recommended.SportRecommended>,

        @ApiModelProperty("直人推荐")
        val lives: List<Recommended.LiveRecommended>

) {
//
//    data class RecommendedPlatform(
//
//            @ApiModelProperty("类别")
//            val category: PlatformCategory,
//
//            @ApiModelProperty("平台")
//            val platform: Platform,
//
//            @ApiModelProperty("logo")
//            val logo: String,
//
//            @ApiModelProperty("鼠标移上去logo")
//            val touchLogo: String
//    )

        data class BannerVo(
                @ApiModelProperty("图标")
                val icon: String
        )

        data class PlatformVo(

                @ApiModelProperty("id")
                val id: Int,

                @ApiModelProperty("平台类型")
                val category: PlatformCategory,

                @ApiModelProperty("平台logo")
                val logo: String,

                @ApiModelProperty("平台名称")
                val name: String,

                @ApiModelProperty("是否启用")
                val status: Status,

                @ApiModelProperty("是否开通")
                val open: Boolean
        )

//    data class LastestVideo(
//            @ApiModelProperty("地址")
//            val path: String,
//
//            @ApiModelProperty("介绍图片")
//            val introductionImage: String
//    )
//
//    data class LiveRecommended(
//
//            @ApiModelProperty("platform")
//            val platform: Platform,
//
//            @ApiModelProperty("logo")
//            val originLogo: String,
//
//            @ApiModelProperty("内容图片")
//            val contentImage: String,
//
//            @ApiModelProperty("标题")
//            val title: String
//    )
//
//    data class FishingRecommended(
//
//            @ApiModelProperty("平台")
//            val platform: Platform,
//
//            @ApiModelProperty("内容图片")
//            val contentImage: String,
//
//            @ApiModelProperty("内容")
//            val content: String
//    )




}
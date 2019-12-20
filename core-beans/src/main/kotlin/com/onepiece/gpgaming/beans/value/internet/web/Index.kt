package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.I18nContent
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
        val recommendedPlatforms: List<RecommendedPlatform>,

        @ApiModelProperty("视频介绍")
        val recommendedVideos: List<VideoRecommended>,

        @ApiModelProperty("体育推荐")
        val sports: List<SportRecommended>,

        @ApiModelProperty("直人推荐")
        val lives: List<LiveRecommended>

) {

        data class RecommendedPlatform(
                @ApiModelProperty("平台")
                val platform: Platform

        ) {

                val category: PlatformCategory
                        @ApiModelProperty("类别")
                        get() = platform.detail.category

                val logo: String
                        @ApiModelProperty("logo")
                        get() = platform.detail.originIcon

                val touchLogo: String
                        @ApiModelProperty("鼠标移上去logo")
                        get() = platform.detail.originIconOver

        }

        data class BannerVo(
                @ApiModelProperty("图标")
                val icon: String
        )

        data class PlatformVo(

                @ApiModelProperty("id")
                val id: Int,

                @ApiModelProperty("平台")
                val platform: Platform,



                @ApiModelProperty("是否启用")
                val status: Status,

                @ApiModelProperty("是否开通")
                val open: Boolean
        ) {

                val demo: Boolean
                        @ApiModelProperty("平台类型")
                        get() = platform.detail.demo

                val category: PlatformCategory
                        @ApiModelProperty("平台类型")
                        get() = platform.detail.category

                val logo: String
                        @ApiModelProperty("平台logo")
                        get() = platform.detail.icon

                val name: String
                        @ApiModelProperty("平台名称")
                        get() = platform.detail.name
        }

        data class VideoRecommended(
                @ApiModelProperty("地址")
                val path: String,

                @ApiModelProperty("视频封面")
                val coverPhoto: String,

                @ApiModelProperty("介绍图片")
                val introductionImage: String
        )

        data class LiveRecommended(

                @ApiModelProperty("platform")
                val platform: Platform,


                @ApiModelProperty("内容图片")
                val contentImage: String,

                @ApiModelProperty("标题")
                val title: String
        ) {


                val originLogo: String
                        @ApiModelProperty("logo")
                        get() = platform.detail.originIconOver

        }

        data class SportRecommended(

                // 平台
                val platform: Platform,

                // 介绍图片
                val contentImage: String

        )
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
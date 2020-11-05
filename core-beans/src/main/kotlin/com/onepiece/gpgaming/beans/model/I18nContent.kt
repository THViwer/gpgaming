package com.onepiece.gpgaming.beans.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime
import java.util.*


/**
 * 国际化配置内容配置
 */
data class I18nContent (

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 配置Id
        val configId: Int,

        // 配置类型
        val configType: I18nConfig,

        // 语言
        val language: Language,

        // 内容
        val contentJson: String,

        // 创建时间
        val createdTime: LocalDateTime,

        // 状态
        val status: Status

) {

    @JsonIgnore
    fun getII18nContent(objectMapper: ObjectMapper): II18nContent {
        return when (configType) {
            I18nConfig.Announcement -> objectMapper.readValue<AnnouncementI18n>(contentJson)
            I18nConfig.AnnouncementDialog -> objectMapper.readValue<AnnouncementDialogI18n>(contentJson)
            I18nConfig.Banner -> objectMapper.readValue<BannerI18n>(contentJson)
            I18nConfig.IndexVideo -> objectMapper.readValue<IndexVideoI18n>(contentJson)
            I18nConfig.Promotion -> objectMapper.readValue<PromotionI18n>(contentJson)
            I18nConfig.IndexSport -> objectMapper.readValue<IndexSportI18n>(contentJson)
            I18nConfig.HotGame -> objectMapper.readValue<HotGameI18n>(contentJson)

            I18nConfig.RegisterSide,
            I18nConfig.Blog,
            I18nConfig.AgentPlans -> objectMapper.readValue<DefaultContentI18n>(contentJson)
        }
    }


    interface II18nContent

    data class BannerI18n(

            val imagePath: String = "",

            // pc 的banner图片地址
            val pcImagePath: String? = null,

            val mobileImagePath: String? = null,

            val title: String?,

            // 介绍
            val introduce: String?
    ): II18nContent

    /**
     * 公告
     */
    data class AnnouncementI18n(

            val title: String,

            val content: String

    ): II18nContent

    data class AnnouncementDialogI18n(

            val title: String,

            val content: String,

            val nonce: String = UUID.randomUUID().toString()
    ): II18nContent

    /**
     * 优惠活动
     */
    data class PromotionI18n(

            // banner
            val banner: String,

            // mobile banner
            val mobileBanner: String,

            // 标题
            val title: String,

            // 内容
            val content: String,

            // 简介
            val synopsis: String?,

            // 注意事项
            val precautions: String?,

            // 最后的优惠banner图 只用于unclejay的wap版
            val latestPromotionBanner: String?

    ): II18nContent

    /**
     * 首页体育
     */
    data class IndexSportI18n(
            // 介绍图片
            val contentImage: String,

            // wap介绍图片
            val wapContentImage: String?

    ): II18nContent

    /**
     * 首页视频
     */
    data class IndexVideoI18n(

            // 视频地址
            val path: String,

            // 视频图片
            val coverPhoto: String,

            // 介绍图片
            val introductionImage: String
    ): II18nContent

    /**
     * 热门游戏
     */
    data class HotGameI18n(

            // 名称
            val name: String,

            // 介绍
            val introduce: String,

            // 图片1
            val img1: String,

            // 图片2
            val img2: String?,

            // 图片3
            val img3: String?
    ): II18nContent

    /**
     * 默认国际化内容
     */
    data class DefaultContentI18n(

            // 标题
            val title: String,

            // 子标题
            val subTitle: String,

            // 内容
            val content: String
    ): II18nContent

}

//fun main() {
//
//    val banner = I18nContent.BannerI18n(imagePath = "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002010684576.jpg")
//    val json = jacksonObjectMapper().writeValueAsString(banner)
//    println(json)

//    val indexVideoI18n = I18nContent.IndexVideoI18n(path = "xxx.mp4", coverPhoto = "xxx.png", introductionImage = "xxxx2.png")
//    val json = jacksonObjectMapper().writeValueAsString(indexVideoI18n)
//    println(json)

//        val indexVideoI18n = I18nContent.IndexSportI18n(platform = Platform.CMD, contentImage = "sfsfa.png")
//    val json = jacksonObjectMapper().writeValueAsString(indexVideoI18n)
//    println(json)

//    val promotion = I18nContent.PromotionI18n(
//            banner = "https://s3.ap-southeast-1.amazonaws.com/awspg1/banner/banner_3.jpg",
//            title = "老虎机优惠",
//            content = "你好，我是老虎机",
//            synopsis = "你好，我是老虎机",
//            precautions = "你好，我是老虎机"
//    )
//    println(jacksonObjectMapper().writeValueAsString(promotion))
//
//}

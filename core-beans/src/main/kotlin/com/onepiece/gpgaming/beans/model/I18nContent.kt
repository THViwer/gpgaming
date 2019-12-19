package com.onepiece.gpgaming.beans.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import java.time.LocalDateTime


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
        val createdTime: LocalDateTime

) {

    @JsonIgnore
    fun getII18nContent(objectMapper: ObjectMapper): II18nContent {
        return when (configType) {
            I18nConfig.Announcement -> objectMapper.readValue<AnnouncementI18n>(contentJson)
            I18nConfig.Banner -> objectMapper.readValue<BannerI18n>(contentJson)
            I18nConfig.IndexVideo -> objectMapper.readValue<IndexVideoI18n>(contentJson)
            I18nConfig.Promotion -> objectMapper.readValue<PromotionI18n>(contentJson)
            I18nConfig.IndexSport -> objectMapper.readValue<IndexSportI18n>(contentJson)
        }
    }


    interface II18nContent

    data class BannerI18n(

            val imagePath: String
    ): II18nContent

    /**
     * 公告
     */
    data class AnnouncementI18n(

            val title: String,

            val content: String

    ): II18nContent

    /**
     * 优惠活动
     */
    data class PromotionI18n(

            // banner
            val banner: String,

            // 标题
            val title: String,

            // 内容
            val content: String,

            // 简介
            val synopsis: String?,

            // 注意事项
            val precautions: String?
    ): II18nContent

    /**
     * 首页体育
     */
    data class IndexSportI18n(

            // 图片地址
            val imagePath: String

    ): II18nContent

    /**
     * 首页视频
     */
    data class IndexVideoI18n(

            // 视频地址
            val videoPath: String,

            // 介绍图片
            val introductionImage: String
    ): II18nContent



}

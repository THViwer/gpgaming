package com.onepiece.gpgaming.beans.value.internet.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.model.I18nContent

sealed class I18nContentWebValue {

    data class I18nContentCoReq(
            // 语言
            val language: Language,

            // 配置Id
            val configId: Int,

            // 配置类型
            val configType: I18nConfig,

            // 内容
            val contentJson: String

    ) {

        fun getI18nContent(objectMapper: ObjectMapper): I18nContent.II18nContent {
            return when (configType) {
                I18nConfig.Banner -> objectMapper.readValue<I18nContent.BannerI18n>(contentJson)
                I18nConfig.IndexSport -> objectMapper.readValue<I18nContent.IndexSportI18n>(contentJson)
                I18nConfig.Promotion -> objectMapper.readValue<I18nContent.PromotionI18n>(contentJson)
                I18nConfig.IndexVideo -> objectMapper.readValue<I18nContent.IndexVideoI18n>(contentJson)
                I18nConfig.Announcement -> objectMapper.readValue<I18nContent.AnnouncementI18n>(contentJson)
                I18nConfig.HotGame -> objectMapper.readValue<I18nContent.HotGameI18n>(contentJson)
            }
        }
    }

    data class I18nContentUoReq(

            val id: Int,

            // 配置类型
            val configType: I18nConfig,

            // 内容
            val contentJson: String

    ) {

        fun getI18nContent(objectMapper: ObjectMapper): I18nContent.II18nContent {
            return when (configType) {
                I18nConfig.Banner -> objectMapper.readValue<I18nContent.BannerI18n>(contentJson)
                I18nConfig.IndexSport -> objectMapper.readValue<I18nContent.IndexSportI18n>(contentJson)
                I18nConfig.Promotion -> objectMapper.readValue<I18nContent.PromotionI18n>(contentJson)
                I18nConfig.IndexVideo -> objectMapper.readValue<I18nContent.IndexVideoI18n>(contentJson)
                I18nConfig.Announcement -> objectMapper.readValue<I18nContent.AnnouncementI18n>(contentJson)
                I18nConfig.HotGame -> objectMapper.readValue<I18nContent.HotGameI18n>(contentJson)
            }
        }
    }





}
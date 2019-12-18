package com.onepiece.treasure.beans.value.internet.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.model.I18nContent

sealed class I18nContentWebValue {

    data class I18nContentCoReq(
            // 语言
            val language: Language,

            // 配置Id
            val configId: Int,

            // 配置类型
            val configType: I18nConfig,

            // 内容
            val content: String

    ) {

        fun getI18nContent(objectMapper: ObjectMapper): I18nContent.II18nContent {
            return when (configType) {
                I18nConfig.Banner -> objectMapper.readValue<I18nContent.BannerI18n>(content)
                I18nConfig.IndexSport -> objectMapper.readValue<I18nContent.IndexSportI18n>(content)
                I18nConfig.Promotion -> objectMapper.readValue<I18nContent.PromotionI18n>(content)
                I18nConfig.IndexVideo -> objectMapper.readValue<I18nContent.IndexVideoI18n>(content)
                I18nConfig.Announcement -> objectMapper.readValue<I18nContent.AnnouncementI18n>(content)
            }
        }
    }

    data class I18nContentUoReq(

            val id: Int,

            // 配置类型
            val configType: I18nConfig,

            // 内容
            val content: String

    ) {

        fun getI18nContent(objectMapper: ObjectMapper): I18nContent.II18nContent {
            return when (configType) {
                I18nConfig.Banner -> objectMapper.readValue<I18nContent.BannerI18n>(content)
                I18nConfig.IndexSport -> objectMapper.readValue<I18nContent.IndexSportI18n>(content)
                I18nConfig.Promotion -> objectMapper.readValue<I18nContent.PromotionI18n>(content)
                I18nConfig.IndexVideo -> objectMapper.readValue<I18nContent.IndexVideoI18n>(content)
                I18nConfig.Announcement -> objectMapper.readValue<I18nContent.AnnouncementI18n>(content)
            }
        }
    }





}
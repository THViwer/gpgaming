package com.onepiece.gpgaming.beans.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.RecommendedType
import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

data class Recommended(

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 推荐类型
        val type: RecommendedType,

        // 内容 json格式
        val contentJson: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime

) {

    @JsonIgnore
    fun getRecommendedContent(objectMapper: ObjectMapper): IRecommended {
        return when (type) {
            RecommendedType.IndexPlatform -> objectMapper.readValue<RecommendedPlatform>(contentJson)
            RecommendedType.IndexSport -> DefaultIRecommended()
            RecommendedType.IndexVideo -> DefaultIRecommended()
            RecommendedType.IndexLive -> objectMapper.readValue<LiveRecommended>(contentJson)
        }
    }


    interface IRecommended

    class DefaultIRecommended: IRecommended

    data class RecommendedPlatform(

            val platforms: List<Platform>

    ): IRecommended

//    data class VideoRecommended(
//            // 地址
//            val path: String,
//
//            // 封面照片
//            val coverPhoto: String,
//
//            // 介绍图片
//            val introductionImage: String
//    ): IRecommended

    data class LiveRecommended(

            // platform
            val platform: Platform,

            // 标题
            val title: String,

            // 内容图片
            val contentImage: String

    ): IRecommended {

        val originLogo: String
            @JsonIgnore
            get() = platform.detail.originIconOver

    }
//
//    data class IndexSportI18n(
//
//            // 平台
//            val platform: Platform,
//
//            // 介绍图片
//            val contentImage: String
//
//    ): I18nContent.II18nContent

//    data class SportRecommended(
//
//            // 平台
//            val platform: Platform
//
////             内容图片
////            val contentImage: String
//
//    ): IRecommended

}

fun main() {

//    val data = Recommended.RecommendedPlatform(platforms = listOf(Platform.SaGaming, Platform.Pragmatic))
//    val json = jacksonObjectMapper().writeValueAsString(data)
//    println(json)

    val data = Recommended.LiveRecommended(platform = Platform.SexyGaming, title = "hello", contentImage = "xxb.png")
    println(jacksonObjectMapper().writeValueAsString(data))




}
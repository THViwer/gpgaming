package com.onepiece.gpgaming.beans.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.RecommendedType
import com.onepiece.gpgaming.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
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

    @ApiModelProperty("国际化内容")
    var i18nContents: List<I18nContent> = emptyList()

    @JsonIgnore
    fun getRecommendedContent(objectMapper: ObjectMapper): IRecommended {
        return when (type) {
            RecommendedType.IndexPlatform -> objectMapper.readValue<RecommendedPlatform>(contentJson)
            RecommendedType.IndexSport -> objectMapper.readValue<RecommendedSport>(contentJson)
            RecommendedType.IndexVideo -> DefaultIRecommended()
            RecommendedType.IndexLive -> objectMapper.readValue<LiveRecommended>(contentJson)
        }
    }


    interface IRecommended

    class DefaultIRecommended: IRecommended

    data class RecommendedPlatform(

            val platforms: List<Platform>

    ): IRecommended


    data class RecommendedSport(

            val platform: Platform

    ): IRecommended


    data class LiveRecommended(

            // platform
            val platform: Platform,

            // 标题
            val title: String,

            // 内容图片
            val contentImage: String

    ): IRecommended {

//        val originLogo: String
//            @JsonIgnore
//            get() = platform.detail.originIconOver

    }

}
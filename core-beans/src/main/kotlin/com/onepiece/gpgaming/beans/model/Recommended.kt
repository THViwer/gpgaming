package com.onepiece.gpgaming.beans.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
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

    @JsonIgnore
    fun getRecommendedContent(objectMapper: ObjectMapper): IRecommended {
        return when (type) {
            RecommendedType.IndexPlatform -> objectMapper.readValue<RecommendedPlatform>(contentJson)
            RecommendedType.IndexSport -> objectMapper.readValue<VideoRecommended>(contentJson)
            RecommendedType.IndexVideo -> objectMapper.readValue<LiveRecommended>(contentJson)
            RecommendedType.IndexLive -> objectMapper.readValue<SportRecommended>(contentJson)
        }
    }


    interface IRecommended

    data class RecommendedPlatform(

            @ApiModelProperty("类别")
            val category: PlatformCategory,

            @ApiModelProperty("平台")
            val platform: Platform,

            @ApiModelProperty("logo")
            val logo: String,

            @ApiModelProperty("鼠标移上去logo")
            val touchLogo: String

    ): IRecommended

    data class VideoRecommended(
            @ApiModelProperty("地址")
            val path: String,

            @ApiModelProperty("介绍图片")
            val introductionImage: String
    ): IRecommended

    data class LiveRecommended(

            @ApiModelProperty("platform")
            val platform: Platform,

            @ApiModelProperty("logo")
            val originLogo: String,

            @ApiModelProperty("内容图片")
            val contentImage: String,

            @ApiModelProperty("标题")
            val title: String
    ): IRecommended

    data class SportRecommended(

//            @ApiModelProperty("平台")
//            val platform: Platform,

            @ApiModelProperty("内容图片")
            val contentImage: String

//            @ApiModelProperty("内容")
//            val content: String
    ): IRecommended



}
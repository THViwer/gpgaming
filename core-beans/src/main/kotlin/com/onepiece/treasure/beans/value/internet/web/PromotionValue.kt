package com.onepiece.treasure.beans.value.internet.web

import com.fasterxml.jackson.annotation.JsonProperty
import com.onepiece.treasure.beans.enums.*
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime


data class PromotionVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("厅主Id")
        val clientId: Int,

        @ApiModelProperty("优惠类型")
        val category: PromotionCategory,

        @ApiModelProperty("结束时间, 如果为null 则无限时间")
        val stopTime: LocalDateTime?,

        @ApiModelProperty("是否置顶")
        val top: Boolean,

        @ApiModelProperty("图标")
        val icon: String,

        @ApiModelProperty("标题")
        val title: String,

        @ApiModelProperty("简介")
        val synopsis: String?,

        @ApiModelProperty("内容")
        val content: String,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("更新时间")
        val updatedTime: LocalDateTime,

        @ApiModelProperty("国际化内容 ")
        val i18nContents: List<I18nContentVo>,

        @ApiModelProperty("优惠规则")
        val promotionRuleVo: PromotionRuleVo

)

data class PromotionRuleVo(
        @ApiModelProperty("充值送类目")
        val category: PromotionRuleCategory,

        @ApiModelProperty("优惠层级Id 如果为null则是全部")
        val levelId: Int?,

        @ApiModelProperty("规则")
        val ruleJson: String
)

data class PromotionCoReq(

        // 基础设置
        @ApiModelProperty("优惠类型")
        val category: PromotionCategory,

        @ApiModelProperty("平台")
        val platform: Platform,

        @ApiModelProperty("结束时间, 如果为null 则无限时间")
        val stopTime: LocalDateTime?,

        @ApiModelProperty("是否置顶")
        val top: Boolean,

        @ApiModelProperty("图标")
        val icon: String,

        @ApiModelProperty("默认国际化内容配置")
        val i18nContent: PromotionDefaultContent,

        @JsonProperty("优惠规则")
        val PromotionRuleVo: PromotionRuleVo

)

data class PromotionDefaultContent(
        // 国际化设置
        @ApiModelProperty("语言")
        val language: Language,

        @ApiModelProperty("标题")
        val title: String,

        @ApiModelProperty("简介")
        val synopsis: String?,

        @ApiModelProperty("内容")
        val content: String
)

data class PromotionUoReq(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("优惠类型")
        val category: PromotionCategory?,

        @ApiModelProperty("结束时间, 如果为null 则无限时间")
        val stopTime: LocalDateTime?,

        @ApiModelProperty("是否置顶")
        val top: Boolean?,

        @ApiModelProperty("图标")
        val icon: String?,

        @ApiModelProperty("状态")
        val status: Status?,

        @ApiModelProperty("优惠规则")
        val ruleJson: String?
)
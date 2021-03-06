package com.onepiece.gpgaming.beans.value.internet.web

import com.fasterxml.jackson.annotation.JsonFormat
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PromotionCategory
import com.onepiece.gpgaming.beans.enums.PromotionPeriod
import com.onepiece.gpgaming.beans.enums.PromotionRuleType
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.I18nContent
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.time.LocalDateTime

sealed class PromotionValue {

        data class LatestPromotionVo(

                val promotionId: Int,

                val banner: String


        )
}

data class PromotionVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("优惠码")
        val code: String,

        @ApiModelProperty("厅主Id")
        val clientId: Int,

        @ApiModelProperty("优惠类型")
        val category: PromotionCategory,

        @ApiModelProperty("平台")
        val platforms: List<Platform>,

        @ApiModelProperty("周期")
        val period: PromotionPeriod,

        @ApiModelProperty("每个周期最大优惠金额")
        val periodMaxPromotion: BigDecimal,

        @ApiModelProperty("结束时间, 如果为null 则无限时间")
        val stopTime: LocalDateTime?,

        @ApiModelProperty("是否置顶")
        val top: Boolean,

        @ApiModelProperty("优惠层级Id 如果为null则是全部")
        val levelId: List<Int>,

//        @ApiModelProperty("图标")
//        val icon: String,
//
        @ApiModelProperty("标题")
        val title: String,
//
//        @ApiModelProperty("简介")
//        val synopsis: String?,
//
//        @ApiModelProperty("内容")
//        val content: String,

        @ApiModelProperty("排序字段")
        val sequence: Int,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("更新时间")
        val updatedTime: LocalDateTime,

        @ApiModelProperty("国际化内容 ")
        val i18nContents: List<I18nContent>,

        @ApiModelProperty("优惠规则")
        val promotionRuleVo: PromotionRuleVo,

        @ApiModelProperty("是否显示")
        val show: Boolean,

        @ApiModelProperty("是否显示到最后优惠")
        val showLatestPromotion: Boolean,

        @ApiModelProperty("是否显示在转账页面")
        val showTransfer: Boolean

)

data class PromotionRuleVo(
        @ApiModelProperty("充值送类目")
        val ruleType: PromotionRuleType,

        @ApiModelProperty("规则")
        val ruleJson: String
)

data class PromotionCoReq(

        @ApiModelProperty("优惠类型")
        val category: PromotionCategory,

        @ApiModelProperty("是否显示到wap端首页 默认为false")
        val showLatestPromotion: Boolean = false,

        @ApiModelProperty("层级Id")
        val levelId: List<Int>,

        @ApiModelProperty("平台")
        val platforms: List<Platform>,

        @ApiModelProperty("结束时间, 如果为null 则无限时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        val stopTime: LocalDateTime?,

        @ApiModelProperty("优惠周期")
        val period: PromotionPeriod,

        @ApiModelProperty("每天最大的优惠金额")
        val periodMaxPromotion: BigDecimal,

        @ApiModelProperty("是否置顶")
        val top: Boolean,

        @ApiModelProperty("优惠规则")
        val promotionRuleVo: PromotionRuleVo,

        @ApiModelProperty("排序字段")
        val sequence: Int,

        @ApiModelProperty("是否前台显示")
        val show: Boolean,

        @ApiModelProperty("优惠码, 只有类型为ActivationCode时才显示")
        val code: String?,

        @ApiModelProperty("是否显示在转账界面")
        val showTransfer: Boolean
)


data class PromotionUoReq(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("平台")
        val platforms: List<Platform>,

        @ApiModelProperty("优惠类型")
        val category: PromotionCategory?,

        @ApiModelProperty("结束时间, 如果为null 则无限时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        val stopTime: LocalDateTime?,

        @ApiModelProperty("优惠周期")
        val period: PromotionPeriod?,

        @ApiModelProperty("每天最大的优惠金额")
        val periodMaxPromotion: BigDecimal,

        @ApiModelProperty("是否置顶")
        val top: Boolean?,

        @ApiModelProperty("图标")
        val icon: String?,

        @ApiModelProperty("状态")
        val status: Status?,

        @ApiModelProperty("层级Id")
        val levelId: List<Int>?,

        @ApiModelProperty("优惠规则")
        val promotionRuleVo: PromotionRuleVo?,

        @ApiModelProperty("排序字段")
        val sequence: Int,

        @ApiModelProperty("是否前台显示")
        val show: Boolean,

        @ApiModelProperty("是否在最后优惠中显示")
        val showLatestPromotion: Boolean,

        @ApiModelProperty("是否显示在转账界面")
        val showTransfer: Boolean
)

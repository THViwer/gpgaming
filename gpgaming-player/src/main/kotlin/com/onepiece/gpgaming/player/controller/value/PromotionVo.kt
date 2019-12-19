package com.onepiece.gpgaming.player.controller.value

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PromotionCategory
import com.onepiece.gpgaming.beans.enums.PromotionRuleType
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.PromotionRules
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class PromotionVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("厅主Id")
        val clientId: Int,

        @ApiModelProperty("平台")
        val platforms: List<Platform>,

//        @ApiModelProperty("平台名称")
//        val platformName: String?,

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

        @ApiModelProperty("注意事项")
        val precautions: String?,

        @ApiModelProperty("活动状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("优惠规则类型")
        val ruleType: PromotionRuleType,

        @ApiModelProperty("规则条件")
        val rule: PromotionRules.Rule

)
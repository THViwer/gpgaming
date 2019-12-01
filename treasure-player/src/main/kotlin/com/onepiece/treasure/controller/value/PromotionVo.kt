package com.onepiece.treasure.controller.value

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.PromotionCategory
import com.onepiece.treasure.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class PromotionVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("厅主Id")
        val clientId: Int,

        @ApiModelProperty("平台")
        val platform: Platform?,

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

        @ApiModelProperty("活动状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime

)
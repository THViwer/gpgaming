package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.BannerType
import com.onepiece.treasure.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class AdvertVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("排序")
        val order: Int,

        @ApiModelProperty("厅主Id")
        val clientId: Int,

        @ApiModelProperty("图标")
        val icon: String,

        @ApiModelProperty("鼠标移动上去图标")
        val touchIcon: String?,

        @ApiModelProperty("类型")
        val position: BannerType,

        @ApiModelProperty("连接地址")
        val link: String,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("更新时间")
        val updatedTime: LocalDateTime
)

data class AdvertCoReq(

        @ApiModelProperty("排序")
        val order: Int,

        @ApiModelProperty("图标")
        val icon: String,

        @ApiModelProperty("鼠标移动上去图标")
        val touchIcon: String?,

        @ApiModelProperty("类型")
        val position: BannerType,

        @ApiModelProperty("连接地址")
        val link: String

)

data class AdvertUoReq(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("排序")
        val order: Int?,

        @ApiModelProperty("图标")
        val icon: String?,

        @ApiModelProperty("鼠标移动上去图标")
        val touchIcon: String?,

        @ApiModelProperty("类型")
        val position: BannerType?,

        @ApiModelProperty("连接地址")
        val link: String?,

        @ApiModelProperty("状态")
        val status: Status?

)
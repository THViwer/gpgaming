package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.I18nContent
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class BannerVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("排序")
        val order: Int,

        @ApiModelProperty("厅主Id")
        val clientId: Int,

//        @ApiModelProperty("图标")
//        val icon: String,
//
//        @ApiModelProperty("鼠标移动上去图标")
//        val touchIcon: String?,
//
        @ApiModelProperty("类型")
        val type: BannerType,

        @ApiModelProperty("连接地址")
        val link: String,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("更新时间")
        val updatedTime: LocalDateTime,

        @ApiModelProperty("平台类型")
        val platformCategory: PlatformCategory?,

        @ApiModelProperty("国际化内容")
        val contents: List<I18nContent>
)

data class BannerCoReq(

        @ApiModelProperty("排序")
        val order: Int,

//        @ApiModelProperty("图标")
//        val icon: String,

//        @ApiModelProperty("鼠标移动上去图标")
//        val touchIcon: String?,

        @ApiModelProperty("类型")
        val type: BannerType,

        @ApiModelProperty("banner的平台类型")
        val platformCategory: PlatformCategory?,

        @ApiModelProperty("连接地址")
        val link: String

)

data class BannerUoReq(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("排序")
        val order: Int?,

//        @ApiModelProperty("图标")
//        val icon: String?,

//        @ApiModelProperty("鼠标移动上去图标")
//        val touchIcon: String?,

        @ApiModelProperty("类型")
        val type: BannerType?,

        @ApiModelProperty("banner的平台类型")
        val platformCategory: PlatformCategory?,

        @ApiModelProperty("连接地址")
        val link: String?,

        @ApiModelProperty("状态")
        val status: Status?

)
package com.onepiece.treasure.beans.value.internet.web

import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime


data class AnnouncementVo(

        @ApiModelProperty("Id")
        val id: Int,

        @ApiModelProperty("厅主Id")
        val clientId: Int,

        @ApiModelProperty("标题")
        val title: String,

        @ApiModelProperty("内容")
        val content: String,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("更新时间")
        val updatedTime: LocalDateTime

)

data class AnnouncementCoReq(

        @ApiModelProperty("标题")
        val title: String,

        @ApiModelProperty("内容")
        val content: String
)

data class AnnouncementUoReq(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("标题")
        val title: String,

        @ApiModelProperty("内容")
        val content: String

)
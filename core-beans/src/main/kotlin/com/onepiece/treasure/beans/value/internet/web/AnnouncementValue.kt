package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.Language
import io.swagger.annotations.ApiModelProperty


data class AnnouncementVo(

        @ApiModelProperty("Id")
        val id: Int,

        @ApiModelProperty("厅主Id")
        val clientId: Int,

        @ApiModelProperty("标题")
        val title: String,

        @ApiModelProperty("内容")
        val content: String
//
//
//        @ApiModelProperty("国际化配置")
//        val i18nContents: List<I18nContentVo>
)

data class AnnouncementCoReq(

        @ApiModelProperty("标题")
        val title: String,

        @ApiModelProperty("简介")
        val synopsis: String?,

        @ApiModelProperty("内容")
        val content: String,

        @ApiModelProperty("语言")
        val language: Language
)

//data class AnnouncementUoReq(
//
//        @ApiModelProperty("id")
//        val id: Int,
//
//        @ApiModelProperty("标题")
//        val title: String,
//
//        @ApiModelProperty("内容")
//        val content: String
//
//)
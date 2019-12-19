package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import io.swagger.annotations.ApiModelProperty
import springfox.documentation.annotations.ApiIgnore

data class I18nContentVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("标题")
        val title: String,

        @ApiModelProperty("banner")
        val banner: String?,

        @ApiModelProperty("内容")
        val content: String,

        @ApiModelProperty("注意事项")
        val precautions: String?,

        @ApiModelProperty("简介")
        val synopsis: String?,

        @ApiModelProperty("语言")
        val language: Language,

        @ApiIgnore
        val configId: Int?,

        @ApiIgnore
        val configType: I18nConfig

)
package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.I18nContent
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class HotGameVo(

        @ApiModelProperty("游戏Id")
        val gameId: String,

        @ApiModelProperty("游戏平台")
        val platform: Platform,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("国际化内容 ")
        val i18nContents: List<I18nContent>

)
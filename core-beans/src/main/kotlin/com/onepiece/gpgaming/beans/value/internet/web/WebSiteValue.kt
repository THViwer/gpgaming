package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime


object DomainValueFactory {

    fun generatorWebSites(): List<WebSiteVo> {

        val now = LocalDateTime.now()
        val w1 = WebSiteVo(id = 1, domain = "http://www.baidu.com", createdTime = now, status = Status.Normal)
        val w2 = w1.copy(id = 2, domain = "http://www.google.com", status = Status.Stop)
        val w3 = w1.copy(id = 3, domain = "http://www.taobal.com")
        return listOf(w1, w2, w3)
    }

}

data class WebSiteVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("域名地址")
        val domain: String,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime
)

data class SelectCountryResult(

        @ApiModelProperty("域名")
        val domain: String,

        @ApiModelProperty("语言")
        val language: Language

)

data class WebSiteCoReq(

        @ApiModelProperty("域名地址")
        val domain: String

)

data class WebSiteUoReq(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("域名地址")
        val domain: String,

        @ApiModelProperty("状态")
        val status: Status

)
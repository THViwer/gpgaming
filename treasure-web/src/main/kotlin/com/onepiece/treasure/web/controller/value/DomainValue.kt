package com.onepiece.treasure.web.controller.value

import com.onepiece.treasure.core.model.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime


object DomainValueFactory {

    fun generatorDomains(): List<DomainVo> {

        val now = LocalDateTime.now()
        val d1 = DomainVo(id = 1, path = "http://www.baidu.com", status = Status.Normal, createdTime = now)
        val d2 = d1.copy(id = 2, path = "http://www.google.com", status = Status.Normal)
        val d3 = d1.copy(id = 3, path = "http://www.taobao.com", status = Status.Normal)

        return listOf(d1, d2, d3)
    }

}

data class DomainVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("域名地址")
        val path: String,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime
)

data class DomainCo(

        @ApiModelProperty("域名地址")
        val path: String

)

data class DomainUo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("域名地址")
        val path: String?,

        @ApiModelProperty("状态")
        val status: Status?

)
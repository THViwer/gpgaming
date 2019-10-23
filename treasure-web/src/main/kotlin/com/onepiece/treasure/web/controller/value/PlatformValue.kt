package com.onepiece.treasure.web.controller.value

import io.swagger.annotations.ApiModelProperty

object PlatformValueFactory {

    fun generatorPlatforms(): List<PlatformVo> {

        val p1 = PlatformVo(id = 1, name = "AG-老虎机", status = true, open = true)
        val p2 = p1.copy(id = -1, name = "SUN-捕鱼", open = false)
        val p3 = p1.copy(id = 2, name = "X-真人")
        val p4 = p1.copy(id = 3, name = "X-体育")

        return listOf(p1, p2, p3, p4)
    }

}

data class PlatformVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("平台名称")
        val name: String,

        @ApiModelProperty("是否启用")
        val status: Boolean,

        @ApiModelProperty("是否开通")
        val open: Boolean
)

data class PlatformUo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("是否启用")
        val status: Boolean
)

package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Status
import io.swagger.annotations.ApiModelProperty

object PlatformValueFactory {
//
//    fun generatorPlatforms(): List<PlatformVo> {
//
//        val p1 = PlatformVo(id = 1, name = "AG-老虎机", category = PlatformCategory.Slot, status = true, open = true)
//        val p2 = p1.copy(id = -1, category = PlatformCategory.Fishing, name = "SUN-捕鱼", open = false)
//        val p3 = p1.copy(id = 2, category = PlatformCategory.LiveVideo, name = "X-真人")
//        val p4 = p1.copy(id = 3, category = PlatformCategory.Sport, name = "X-体育")
//
//        return listOf(p1, p2, p3, p4)
//    }

}

data class PlatformVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("平台类型")
        val category: PlatformCategory,

        @ApiModelProperty("平台logo")
        val logo: String,

        @ApiModelProperty("平台名称")
        val name: String,

        @ApiModelProperty("是否启用")
        val status: Status,

        @ApiModelProperty("是否开通")
        val open: Boolean
)

data class PlatformUoReq(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("是否启用")
        val status: Status
)
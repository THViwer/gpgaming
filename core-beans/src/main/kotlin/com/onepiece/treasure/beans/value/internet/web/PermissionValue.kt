package com.onepiece.treasure.beans.value.internet.web

import io.swagger.annotations.ApiModelProperty

object PermissionValueFactory {

    fun generatorPermissionVo(): List<PermissionVo> {

        val p1 = PermissionVo(resourceId = 1100, check = true)
        val p1_1 = p1.copy(resourceId = 1101, check = true)
        val p1_2 = p1.copy(resourceId = 1102, check = false)

        val p2 = p1.copy(resourceId = 2100, check = true)
        val p2_1 = p2.copy(resourceId = 2101, check = false)
        val p2_2 = p2.copy(resourceId = 2102, check = false)
        val p2_3 = p2.copy(resourceId = 2103, check = true)

        return listOf(p1, p1_1, p1_2, p2, p2_1, p2_2, p2_3)
    }


}

data class PermissionVo(

        @ApiModelProperty("资源Id")
        val resourceId: Int,

        @ApiModelProperty("是否有权限")
        val check: Boolean
)

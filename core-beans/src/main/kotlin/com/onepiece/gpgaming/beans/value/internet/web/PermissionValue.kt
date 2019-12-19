package com.onepiece.gpgaming.beans.value.internet.web

import io.swagger.annotations.ApiModelProperty

//object PermissionValueFactory {
//
//    fun generatorPermissionVo(): List<PermissionVo> {
//
//        val p1 = PermissionVo(resourceId = 1100, effective = true)
//        val p1_1 = p1.copy(resourceId = 1101, effective = true)
//        val p1_2 = p1.copy(resourceId = 1102, effective = false)
//
//        val p2 = p1.copy(resourceId = 2100, effective = true)
//        val p2_1 = p2.copy(resourceId = 2101, effective = false)
//        val p2_2 = p2.copy(resourceId = 2102, effective = false)
//        val p2_3 = p2.copy(resourceId = 2103, effective = true)
//
//        return listOf(p1, p1_1, p1_2, p2, p2_1, p2_2, p2_3)
//    }
//
//
//}

sealed class PermissionValue {

    data class PermissionReq(

            @ApiModelProperty("客服Id")
            val waiterId: Int,

            @ApiModelProperty("权限列表")
            val permissions: List<PermissionVo>
    )

    data class PermissionVo(

            @ApiModelProperty("父节点Id")
            val parentId: String,

            @ApiModelProperty("资源Id")
            val resourceId: String,

            @ApiModelProperty("资源名称")
            val name: String,

            @ApiModelProperty("是否有权限")
            val effective: Boolean,

            @ApiModelProperty("子权限列表")
            val permissions: List<PermissionVo>?
    )


}

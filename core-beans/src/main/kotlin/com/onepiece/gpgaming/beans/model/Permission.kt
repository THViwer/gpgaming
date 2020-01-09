package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

/**
 * 权限
 */
data class Permission(

        // id
        val id: Int,

        // 客服Id
        val waiterId: Int,

        // 权限字符串
        val permissions: List<PermissionDetail>,

        // 创建时间
        val createdTime: LocalDateTime,

        // 状态
        val status: Status
)

data class PermissionDetail(

        // 权限Id
        val resourceId: String,

        // 是否有效
        val effective: Boolean
)
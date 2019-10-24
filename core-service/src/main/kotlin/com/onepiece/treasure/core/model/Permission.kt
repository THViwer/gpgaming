package com.onepiece.treasure.core.model

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
        val createdTime: LocalDateTime
)

data class PermissionDetail(

        // 权限Id
        val permissionId: Int,

        // 是否有效
        val effective: Boolean
)
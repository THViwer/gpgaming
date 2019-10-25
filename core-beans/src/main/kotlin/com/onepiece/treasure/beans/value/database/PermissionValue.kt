package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.model.PermissionDetail

data class PermissionCo(
        // 客服Id
        val waiterId: Int,

        // 权限Id
        val permissions: List<PermissionDetail>
)

data class PermissionUo(
        // 客服Id
        val waiterId: Int,

        // 权限Id
        val permissions: List<PermissionDetail>
)
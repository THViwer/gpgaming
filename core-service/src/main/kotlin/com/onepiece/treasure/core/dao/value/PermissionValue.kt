package com.onepiece.treasure.core.dao.value

import com.onepiece.treasure.core.model.PermissionDetail

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
package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.PermissionCo
import com.onepiece.treasure.core.dao.value.PermissionUo
import com.onepiece.treasure.core.model.Permission

interface PermissionDao: BasicDao<Permission> {

    fun create(permissionCo: PermissionCo): Boolean

    fun update(permissionUo: PermissionUo): Boolean

}
package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.PermissionCo
import com.onepiece.treasure.beans.value.database.PermissionUo
import com.onepiece.treasure.beans.model.Permission

interface PermissionDao: BasicDao<Permission> {

    fun create(permissionCo: PermissionCo): Boolean

    fun update(permissionUo: PermissionUo): Boolean

}
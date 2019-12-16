package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.Permission
import com.onepiece.treasure.beans.value.database.PermissionUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface PermissionDao: BasicDao<Permission> {

    fun findWaiterPermissions(waiterId: Int): Permission?

    fun create(permissionUo: PermissionUo): Boolean

    fun update(permissionUo: PermissionUo): Boolean

}
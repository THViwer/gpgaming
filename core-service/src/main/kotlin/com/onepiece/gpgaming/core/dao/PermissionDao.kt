package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Permission
import com.onepiece.gpgaming.beans.value.database.PermissionUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface PermissionDao: BasicDao<Permission> {

    fun findWaiterPermissions(waiterId: Int): Permission?

    fun create(permissionUo: PermissionUo): Boolean

    fun update(permissionUo: PermissionUo): Boolean

}
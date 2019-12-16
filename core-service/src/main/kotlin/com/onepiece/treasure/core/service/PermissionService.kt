package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Permission
import com.onepiece.treasure.beans.value.database.PermissionUo

interface PermissionService {

    fun findWaiterPermissions(waiterId: Int): Permission

    fun create(permissionUo: PermissionUo)

    fun update(permissionUo: PermissionUo)
}
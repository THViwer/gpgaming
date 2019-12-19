package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.Permission
import com.onepiece.gpgaming.beans.value.database.PermissionUo

interface PermissionService {

    fun findWaiterPermissions(waiterId: Int): Permission

    fun create(permissionUo: PermissionUo)

    fun update(permissionUo: PermissionUo)
}
package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Permission
import com.onepiece.treasure.beans.value.database.PermissionCo
import com.onepiece.treasure.beans.value.database.PermissionUo
import com.onepiece.treasure.core.dao.PermissionDao
import com.onepiece.treasure.core.service.PermissionService
import org.springframework.stereotype.Service

@Service
class PermissionServiceImpl(
        private val permissionDao: PermissionDao
) : PermissionService {

    override fun findWaiterPermissions(waiterId: Int): Permission {
        return permissionDao.findWaiterPermissions(waiterId)
    }

    override fun create(permissionCo: PermissionCo) {
        val state = permissionDao.create(permissionCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(permissionUo: PermissionUo) {
        val state = permissionDao.update(permissionUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}
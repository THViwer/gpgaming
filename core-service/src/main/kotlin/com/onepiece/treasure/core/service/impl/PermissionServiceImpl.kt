package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Permission
import com.onepiece.treasure.beans.value.database.PermissionCo
import com.onepiece.treasure.beans.value.database.PermissionUo
import com.onepiece.treasure.core.dao.PermissionDao
import com.onepiece.treasure.core.service.PermissionService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PermissionServiceImpl(
        private val permissionDao: PermissionDao
) : PermissionService {

    override fun findWaiterPermissions(waiterId: Int): Permission {
        return permissionDao.findWaiterPermissions(waiterId)?:
                Permission(id = -1, waiterId = waiterId, permissions = emptyList(), createdTime = LocalDateTime.now())
    }

    override fun create(permissionUo: PermissionUo) {
        val state = permissionDao.create(permissionUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(permissionUo: PermissionUo) {
        val hasPermission = permissionDao.findWaiterPermissions(permissionUo.waiterId)

        if (hasPermission == null) {
            val state = permissionDao.create(permissionUo)
            check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
        } else {
            val state = permissionDao.update(permissionUo)
            check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
        }

    }
}
package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Waiter
import com.onepiece.treasure.beans.value.database.LoginValue
import com.onepiece.treasure.beans.value.database.PermissionCo
import com.onepiece.treasure.beans.value.database.WaiterCo
import com.onepiece.treasure.beans.value.database.WaiterUo
import com.onepiece.treasure.core.dao.WaiterDao
import com.onepiece.treasure.core.service.PermissionService
import com.onepiece.treasure.core.service.WaiterService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class WaiterServiceImpl(
        private val waiterDao: WaiterDao,
        private val permissionService: PermissionService
) : WaiterService {

    override fun get(id: Int): Waiter {
        return waiterDao.get(id)
    }

    override fun findClientWaiters(clientId: Int): List<Waiter> {
        return waiterDao.all(clientId)
    }

    override fun login(loginValue: LoginValue): Waiter {

        val waiter = waiterDao.findByUsername(loginValue.username)
        checkNotNull(waiter) { OnePieceExceptionCode.LOGIN_FAIL }
        check(loginValue.password == waiter.password) { OnePieceExceptionCode.LOGIN_FAIL }
        check(waiter.status == Status.Normal) { OnePieceExceptionCode.USER_STOP }

        // update client
        val waiterUo = WaiterUo(id = waiter.id, loginIp = loginValue.ip, loginTime = LocalDateTime.now())
        this.update(waiterUo)

        return waiter.copy(password = "")
    }

    override fun create(waiterCo: WaiterCo) {
        val id = waiterDao.create(waiterCo)
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // 创建权限
        val permissionCo = PermissionCo(waiterId = id, permissions = emptyList())
        permissionService.create(permissionCo)
    }

    override fun update(waiterUo: WaiterUo) {
        val state = waiterDao.update(waiterUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}
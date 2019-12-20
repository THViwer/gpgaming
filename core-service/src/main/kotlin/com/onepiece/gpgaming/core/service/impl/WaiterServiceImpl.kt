package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Waiter
import com.onepiece.gpgaming.beans.value.database.LoginValue
import com.onepiece.gpgaming.beans.value.database.PermissionUo
import com.onepiece.gpgaming.beans.value.database.WaiterCo
import com.onepiece.gpgaming.beans.value.database.WaiterUo
import com.onepiece.gpgaming.core.dao.WaiterDao
import com.onepiece.gpgaming.core.service.PermissionService
import com.onepiece.gpgaming.core.service.WaiterService
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
        val waiterUo = WaiterUo(id = waiter.id, loginIp = loginValue.ip, loginTime = LocalDateTime.now(), clientBankData = null)
        this.update(waiterUo)

        return waiter.copy(password = "")
    }

    override fun create(waiterCo: WaiterCo) {
        val id = waiterDao.create(waiterCo)
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // 创建权限
        val permissionUo = PermissionUo(waiterId = id, permissions = emptyList())
        permissionService.create(permissionUo)
    }

    override fun update(waiterUo: WaiterUo) {
        val state = waiterDao.update(waiterUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}
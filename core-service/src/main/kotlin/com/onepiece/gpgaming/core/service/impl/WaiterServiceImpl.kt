package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Waiter
import com.onepiece.gpgaming.beans.value.database.ClientLoginValue
import com.onepiece.gpgaming.beans.value.database.LoginHistoryValue
import com.onepiece.gpgaming.beans.value.database.PermissionUo
import com.onepiece.gpgaming.beans.value.database.WaiterValue
import com.onepiece.gpgaming.core.dao.WaiterDao
import com.onepiece.gpgaming.core.service.LoginHistoryService
import com.onepiece.gpgaming.core.service.PermissionService
import com.onepiece.gpgaming.core.service.WaiterService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class WaiterServiceImpl(
        private val waiterDao: WaiterDao,
        private val permissionService: PermissionService,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val redisService: RedisService,
        private val loginHistoryService: LoginHistoryService
) : WaiterService {

    override fun all(role: Role?): List<Waiter> {
        return waiterDao.all().filter { role == null || role == it.role }
    }

    override fun get(id: Int): Waiter {
        return waiterDao.get(id)
    }

    override fun findClientWaiters(clientId: Int): List<Waiter> {
        return waiterDao.all(clientId)
    }

    override fun login(loginValue: ClientLoginValue.ClientLoginReq): Waiter {

        val waiter = waiterDao.findByUsername(loginValue.username)
        checkNotNull(waiter) { OnePieceExceptionCode.LOGIN_FAIL }
        check(loginValue.clientId == waiter.clientId) { OnePieceExceptionCode.LOGIN_FAIL }
        check(bCryptPasswordEncoder.matches(loginValue.password, waiter.password)) { OnePieceExceptionCode.LOGIN_FAIL }
        check(waiter.status == Status.Normal) { OnePieceExceptionCode.USER_STOP }
        check(loginValue.clientId == waiter.clientId) { OnePieceExceptionCode.LOGIN_FAIL }

        // update client
        val waiterUo = WaiterValue.WaiterUo(id = waiter.id, loginIp = loginValue.ip, loginTime = LocalDateTime.now(), clientBankData = null)
        this.update(waiterUo)

        val historyCo = LoginHistoryValue.LoginHistoryCo(bossId = waiter.bossId, clientId = waiter.clientId, userId = waiter.id,
                ip = loginValue.ip, role = waiter.role, username = loginValue.username, deviceType = "pc")
        loginHistoryService.create(historyCo)

        return waiter.copy(password = "")
    }

    override fun create(waiterCo: WaiterValue.WaiterCo) {
        val password = bCryptPasswordEncoder.encode(waiterCo.password)
        val id = waiterDao.create(waiterCo.copy(password = password))
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // 创建权限
        val permissionUo = PermissionUo(waiterId = id, permissions = emptyList())
        permissionService.create(permissionUo)
    }

    override fun findByUsername(clientId: Int, username: String?): Waiter? {

        if (username.isNullOrBlank()) return null

        val waiter = waiterDao.findByUsername(username) ?: return null
        check(waiter.clientId == clientId)
        return waiter
    }

    override fun update(waiterUo: WaiterValue.WaiterUo) {
        val password = waiterUo.password?.let {
            bCryptPasswordEncoder.encode(it)
        }

        val state = waiterDao.update(waiterUo.copy(password = password))
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun selectSale(bossId: Int, clientId: Int, saleId: Int?): Waiter? {

        fun selectNext(): Waiter? {
            val redisKey = "salesman:id:$clientId"
            val cacheSaleId = redisService.get(key = redisKey, clz = Int::class.java) ?: -1
            return waiterDao.all(clientId = clientId).filter { bossId == it.bossId }.filter { it.status == Status.Normal }.filter { it.role == Role.Sale }
                    .let { list ->
                        list.firstOrNull { it.id > cacheSaleId } ?: list.firstOrNull()
                    }?.also {
                        redisService.put(key = redisKey, value = it.id)
                    }
        }

        return when {
            saleId != null && saleId > 0 -> {
                waiterDao.get(saleId)
            }
            else -> selectNext()
        }
    }

    override fun checkPassword(id: Int, password: String): Boolean {
        val waiter = waiterDao.get(id = id)
        return bCryptPasswordEncoder.matches(password, waiter.password)
    }
}

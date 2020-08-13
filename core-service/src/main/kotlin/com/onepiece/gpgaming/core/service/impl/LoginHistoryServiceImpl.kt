package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.LoginHistory
import com.onepiece.gpgaming.beans.value.database.LoginHistoryValue
import com.onepiece.gpgaming.core.dao.LoginHistoryDao
import com.onepiece.gpgaming.core.service.LoginHistoryService
import org.springframework.stereotype.Service

@Service
class LoginHistoryServiceImpl(
        private val loginHistoryDao: LoginHistoryDao
) : LoginHistoryService {

    override fun create(co: LoginHistoryValue.LoginHistoryCo) {
        val username  = co.username.split("@").last()
        val flag = loginHistoryDao.create(co = co.copy(username = username))
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun list(userId: Int, role: Role): List<LoginHistory> {
        return loginHistoryDao.list(userId = userId, role = role)
    }
}
package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.model.LoginHistory
import com.onepiece.gpgaming.beans.value.database.LoginHistoryValue
import org.springframework.scheduling.annotation.Async

interface LoginHistoryService {

    @Async
    fun create(co: LoginHistoryValue.LoginHistoryCo)

    fun list(userId: Int, role: Role): List<LoginHistory>

}
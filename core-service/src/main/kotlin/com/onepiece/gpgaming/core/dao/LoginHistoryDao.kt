package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.model.LoginHistory
import com.onepiece.gpgaming.beans.value.database.LoginHistoryValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface LoginHistoryDao: BasicDao<LoginHistory> {

    fun create(co: LoginHistoryValue.LoginHistoryCo): Boolean

    fun list(userId: Int, role: Role): List<LoginHistory>

}
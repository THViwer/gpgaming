package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.CommissionType
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Commission
import com.onepiece.gpgaming.beans.value.database.CommissionValue
import com.onepiece.gpgaming.core.dao.CommissionDao
import com.onepiece.gpgaming.core.service.CommissionService
import org.springframework.stereotype.Service

@Service
class CommissionServiceImpl(
        private val commissionDao: CommissionDao
): CommissionService {

    override fun list(bossId: Int, type: CommissionType): List<Commission> {
        return commissionDao.list(bossId = bossId).filter { it.type == type }
    }

    override fun create(co: CommissionValue.CommissionCo) {
        val flag = commissionDao.create(co)
        check(flag) { OnePieceExceptionCode.DATA_FAIL }
    }

    override fun update(uo: CommissionValue.CommissionUo) {
        val flag = commissionDao.update(uo)
        check(flag) { OnePieceExceptionCode.DATA_FAIL }
    }
}
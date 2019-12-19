package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Balance
import com.onepiece.gpgaming.beans.value.database.BalanceCo
import com.onepiece.gpgaming.beans.value.database.BalanceUo
import com.onepiece.gpgaming.core.dao.BalanceDao
import com.onepiece.gpgaming.core.service.BalanceService
import org.springframework.stereotype.Service

@Service
class BalanceServiceImpl(
        private val balanceDao: BalanceDao
) : BalanceService {

    override fun getClientBalance(clientId: Int): Balance {
        return balanceDao.getClientBalance(clientId)
    }

    override fun create(balanceCo: BalanceCo) {
        val state = balanceDao.create(balanceCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(balanceUo: BalanceUo) {
        val state = balanceDao.update(balanceUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}
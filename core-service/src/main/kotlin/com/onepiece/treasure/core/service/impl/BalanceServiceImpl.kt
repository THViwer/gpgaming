package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Balance
import com.onepiece.treasure.beans.value.database.BalanceCo
import com.onepiece.treasure.beans.value.database.BalanceUo
import com.onepiece.treasure.core.dao.BalanceDao
import com.onepiece.treasure.core.service.BalanceService
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
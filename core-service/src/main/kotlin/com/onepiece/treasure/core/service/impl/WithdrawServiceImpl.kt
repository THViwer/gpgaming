package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Withdraw
import com.onepiece.treasure.beans.value.database.WithdrawCo
import com.onepiece.treasure.beans.value.database.WithdrawQuery
import com.onepiece.treasure.beans.value.database.WithdrawUo
import com.onepiece.treasure.core.dao.WithdrawDao
import com.onepiece.treasure.core.service.WithdrawService
import org.springframework.stereotype.Service

@Service
class WithdrawServiceImpl(
        private val withdrawDao: WithdrawDao
) : WithdrawService {

    override fun query(withdrawQuery: WithdrawQuery): List<Withdraw> {
        return withdrawDao.query(withdrawQuery)
    }

    override fun create(withdrawCo: WithdrawCo) {
        val state = withdrawDao.create(withdrawCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(withdrawUo: WithdrawUo) {
        val state = withdrawDao.update(withdrawUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}
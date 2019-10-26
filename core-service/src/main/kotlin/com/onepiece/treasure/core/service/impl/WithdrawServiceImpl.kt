package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Withdraw
import com.onepiece.treasure.beans.value.database.DepositLockUo
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

    override fun findWithdraw(clientId: Int, orderId: String): Withdraw {
        return withdrawDao.findWithdraw(clientId, orderId)
    }

    override fun query(withdrawQuery: WithdrawQuery): List<Withdraw> {
        return withdrawDao.query(withdrawQuery, 0, 1000)
    }

    override fun query(withdrawQuery: WithdrawQuery, current: Int, size: Int): Page<Withdraw> {
        val total = withdrawDao.total(query = withdrawQuery)
        if (total == 0) return Page.empty()

        val data = withdrawDao.query(query = withdrawQuery, current = current, size = size)
        return Page.of(total, data)
    }

    override fun create(withdrawCo: WithdrawCo) {
        val state = withdrawDao.create(withdrawCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun lock(withdrawLockUo: DepositLockUo) {
        val state = withdrawDao.lock(withdrawLockUo)
        check(state) { OnePieceExceptionCode.ORDER_EXPIRED }
    }

    override fun update(withdrawUo: WithdrawUo) {
        val state = withdrawDao.update(withdrawUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        //TODO 操作用户余额
    }
}
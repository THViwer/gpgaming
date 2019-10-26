package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Deposit
import com.onepiece.treasure.beans.value.database.DepositCo
import com.onepiece.treasure.beans.value.database.DepositLockUo
import com.onepiece.treasure.beans.value.database.DepositQuery
import com.onepiece.treasure.beans.value.database.DepositUo
import com.onepiece.treasure.core.dao.DepositDao
import com.onepiece.treasure.core.service.DepositService
import org.springframework.stereotype.Service

@Service
class DepositServiceImpl(
        private val depositDao: DepositDao
) : DepositService {

    override fun findDeposit(clientId: Int, orderId: String): Deposit {
        return depositDao.findDeposit(clientId, orderId)
    }

    override fun query(depositQuery: DepositQuery): List<Deposit> {
        return depositDao.query(query = depositQuery, current = 0, size = 1000)
    }

    override fun query(depositQuery: DepositQuery, current: Int, size: Int): Page<Deposit> {

        val total = depositDao.total(depositQuery)
        if (total == 0) return Page.empty()

        val data = depositDao.query(depositQuery, current, size)
        return Page.of(total, data)
    }

    override fun create(depositCo: DepositCo) {
        val state = depositDao.create(depositCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun lock(depositLockUo: DepositLockUo) {
        val state = depositDao.lock(depositLockUo)
        check(state) { OnePieceExceptionCode.ORDER_EXPIRED }
    }

    override fun update(depositUo: DepositUo) {
        val state = depositDao.update(depositUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        //TODO 操作金额
    }
}
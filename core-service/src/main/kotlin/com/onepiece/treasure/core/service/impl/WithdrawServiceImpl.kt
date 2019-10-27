package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.enums.WalletEvent
import com.onepiece.treasure.beans.enums.WithdrawState
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Withdraw
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.beans.value.internet.web.WithdrawUoReq
import com.onepiece.treasure.core.dao.WithdrawDao
import com.onepiece.treasure.core.service.WalletService
import com.onepiece.treasure.core.service.WithdrawService
import org.springframework.stereotype.Service

@Service
class WithdrawServiceImpl(
        private val withdrawDao: WithdrawDao,
        private val walletService: WalletService
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

        val walletUo = WalletUo(clientId = withdrawCo.clientId, waiterId = null, memberId = withdrawCo.memberId, money = withdrawCo.money,
                eventId = withdrawCo.orderId, event = WalletEvent.FREEZE, remarks = "memberId:${withdrawCo.memberId} freeze")
        walletService.update(walletUo)
    }

    override fun lock(withdrawLockUo: DepositLockUo) {
        val state = withdrawDao.lock(withdrawLockUo)
        check(state) { OnePieceExceptionCode.ORDER_EXPIRED }
    }

    override fun check(withdrawUoReq: WithdrawUoReq) {

        val order = withdrawDao.findWithdraw(withdrawUoReq.clientId, withdrawUoReq.orderId)
        check( order.state == WithdrawState.Process) { OnePieceExceptionCode.ORDER_EXPIRED }

        val withdrawUo = WithdrawUo(orderId = withdrawUoReq.orderId, processId = order.processId, state = withdrawUoReq.state,
                remarks = withdrawUoReq.remarks, clientId = withdrawUoReq.clientId, waiterId = withdrawUoReq.waiterId)
        val state = withdrawDao.check(withdrawUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }


        when (withdrawUoReq.state) {
            WithdrawState.Successful -> {
                val remarks = withdrawUoReq.remarks ?: "waiterId:${withdrawUoReq.waiterId} check"
                val walletUo = WalletUo(clientId = withdrawUoReq.clientId, memberId = order.memberId, money = order.money, event = WalletEvent.WITHDRAW,
                        remarks = remarks, waiterId = withdrawUoReq.waiterId, eventId = order.orderId)
                walletService.update(walletUo)
            }
            WithdrawState.Fail -> {
                val remarks = withdrawUoReq.remarks ?: "waiterId:${withdrawUoReq.waiterId} withdraw fail"
                val walletUo = WalletUo(clientId = withdrawUoReq.clientId, memberId = order.memberId, money = order.money, event = WalletEvent.WITHDRAW_FAIL,
                        remarks = remarks, waiterId = withdrawUoReq.waiterId, eventId = order.orderId)
                walletService.update(walletUo)
            }
            else -> {
                //TODO NOTHING
            }
        }

    }
}
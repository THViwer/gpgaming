package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.beans.enums.WithdrawState
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Withdraw
import com.onepiece.gpgaming.beans.value.database.ClientWithdrawReportVo
import com.onepiece.gpgaming.beans.value.database.DepositLockUo
import com.onepiece.gpgaming.beans.value.database.WalletUo
import com.onepiece.gpgaming.beans.value.database.WithdrawCo
import com.onepiece.gpgaming.beans.value.database.WithdrawQuery
import com.onepiece.gpgaming.beans.value.database.WithdrawReportVo
import com.onepiece.gpgaming.beans.value.database.WithdrawUo
import com.onepiece.gpgaming.beans.value.internet.web.WithdrawValue
import com.onepiece.gpgaming.core.dao.WithdrawDao
import com.onepiece.gpgaming.core.service.WalletService
import com.onepiece.gpgaming.core.service.WithdrawService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
class WithdrawServiceImpl(
        private val withdrawDao: WithdrawDao,
        private val walletService: WalletService
) : WithdrawService {

    override fun findWithdraw(clientId: Int, orderId: String): Withdraw {
        return withdrawDao.findWithdraw(clientId, orderId)
    }

    override fun query(withdrawQuery: WithdrawQuery): List<Withdraw> {
        return withdrawDao.query(withdrawQuery, 0, withdrawQuery.size)
    }

    override fun query(withdrawQuery: WithdrawQuery, current: Int, size: Int): Page<Withdraw> {
        val total = withdrawDao.total(query = withdrawQuery)
        if (total == 0) return Page.empty()

        val data = withdrawDao.query(query = withdrawQuery, current = current, size = size)
        return Page.of(total, data)
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun create(withdrawCo: WithdrawCo) {
        val state = withdrawDao.create(withdrawCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        val wallet = walletService.getMemberWallet(withdrawCo.memberId)
        check(wallet.balance.toDouble() >= withdrawCo.money.toDouble()) { OnePieceExceptionCode.BALANCE_SHORT_FAIL }

        val walletUo = WalletUo(clientId = withdrawCo.clientId, waiterId = null, memberId = withdrawCo.memberId, money = withdrawCo.money,
                eventId = withdrawCo.orderId, event = WalletEvent.FREEZE, remarks = "memberId:${withdrawCo.memberId} freeze")
        walletService.update(walletUo)
    }

    override fun lock(withdrawLockUo: DepositLockUo) {
        val state = withdrawDao.lock(withdrawLockUo)
        check(state) { OnePieceExceptionCode.ORDER_EXPIRED }
    }

    override fun check(withdrawUoReq: WithdrawValue.WithdrawUoReq) {

        val order = withdrawDao.findWithdraw(withdrawUoReq.clientId, withdrawUoReq.orderId)
        check(order.state == WithdrawState.Process) { OnePieceExceptionCode.ORDER_EXPIRED }

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

    override fun report(startDate: LocalDate, endDate: LocalDate): List<WithdrawReportVo> {
        return withdrawDao.report(clientId = null, memberId = null, startDate = startDate, endDate = endDate)
    }

    override fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientWithdrawReportVo> {
        return withdrawDao.reportByClient(startDate, endDate)
    }

    override fun getTotalWithdraw(clientId: Int, memberId: Int, startDate: LocalDate): BigDecimal {
        return withdrawDao.getTotalWithdraw(clientId = clientId, memberId = memberId, startDate = startDate)
    }
}
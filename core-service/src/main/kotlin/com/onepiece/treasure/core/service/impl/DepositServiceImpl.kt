package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.enums.WalletEvent
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Deposit
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.beans.value.database.ClientDepositReportVo
import com.onepiece.treasure.beans.value.database.DepositReportVo
import com.onepiece.treasure.beans.value.internet.web.DepositUoReq
import com.onepiece.treasure.core.dao.DepositDao
import com.onepiece.treasure.core.service.DepositService
import com.onepiece.treasure.core.service.WalletService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DepositServiceImpl(
        private val depositDao: DepositDao,
        private val walletService: WalletService
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

    override fun check(depositUoReq: DepositUoReq) {
        val order = depositDao.findDeposit(depositUoReq.clientId, depositUoReq.orderId)
        check(order.state ==  DepositState.Close || order.state == DepositState.Process) { OnePieceExceptionCode.ORDER_EXPIRED }

        val depositUo = DepositUo(clientId = order.clientId, orderId = order.orderId, processId = order.processId,
                state = depositUoReq.state, remarks = depositUoReq.remarks, lockWaiterId = depositUoReq.waiterId)
        val state = depositDao.update(depositUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        //if state == Successful, deposit money
        if (depositUo.state == DepositState.Successful) {
            val remarks = depositUoReq.remarks?: "waiterId:${depositUoReq.waiterId} check"
            val walletUo = WalletUo(clientId = depositUo.clientId, memberId = order.memberId, event = WalletEvent.DEPOSIT, remarks = remarks,
                    money = order.money, waiterId = depositUoReq.waiterId, eventId = order.orderId)
            walletService.update(walletUo)
        }
    }

    override fun report(startDate: LocalDate, endDate: LocalDate): List<DepositReportVo> {
        return depositDao.report(startDate, endDate)
    }

    override fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientDepositReportVo> {
        return depositDao.reportByClient(startDate, endDate)
    }
}
package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.enums.DepositState
import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Deposit
import com.onepiece.gpgaming.beans.value.database.ClientDepositReportVo
import com.onepiece.gpgaming.beans.value.database.DepositCo
import com.onepiece.gpgaming.beans.value.database.DepositLockUo
import com.onepiece.gpgaming.beans.value.database.DepositQuery
import com.onepiece.gpgaming.beans.value.database.DepositReportVo
import com.onepiece.gpgaming.beans.value.database.DepositUo
import com.onepiece.gpgaming.beans.value.database.FirstDepositVo
import com.onepiece.gpgaming.beans.value.database.WalletUo
import com.onepiece.gpgaming.beans.value.internet.web.DepositValue
import com.onepiece.gpgaming.core.dao.DepositDao
import com.onepiece.gpgaming.core.service.DepositService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.WalletService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class DepositServiceImpl(
        private val depositDao: DepositDao,
        private val walletService: WalletService
) : DepositService {

    @Autowired
    lateinit var memberService: MemberService

    override fun findDeposit(clientId: Int, orderId: String): Deposit {
        return depositDao.findDeposit(clientId, orderId)
    }

    override fun query(depositQuery: DepositQuery): List<Deposit> {
        return depositDao.query(query = depositQuery, current = 0, size = depositQuery.size)
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

    override fun check(depositUoReq: DepositValue.DepositUoReq) {
        val order = depositDao.findDeposit(depositUoReq.clientId, depositUoReq.orderId)
        check(order.state ==  DepositState.Close || order.state == DepositState.Process) { OnePieceExceptionCode.ORDER_EXPIRED }


        val member = memberService.getMember(id = order.memberId)
        val firstDeposit = !member.firstDeposit

        val depositUo = DepositUo(clientId = order.clientId, orderId = order.orderId, processId = order.processId,
                state = depositUoReq.state, remarks = depositUoReq.remarks, lockWaiterId = depositUoReq.waiterId, firstDeposit = firstDeposit)
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
        return depositDao.report(clientId = null, memberId = null, startDate = startDate, endDate = endDate)
    }

    override fun sumSuccessful(clientId: Int, memberId: Int, startDate: LocalDate, endDate: LocalDate): BigDecimal {
        return depositDao.sumSuccessful(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate)
    }

    override fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientDepositReportVo> {
        return depositDao.reportByClient(startDate, endDate)
    }

    override fun queryFirstDepositDetail(startDate: LocalDate): List<FirstDepositVo> {
        return depositDao.queryFirstDepositDetail(startDate = startDate)
    }
}
package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.model.Withdraw
import com.onepiece.gpgaming.beans.value.database.ClientWithdrawReportVo
import com.onepiece.gpgaming.beans.value.database.DepositLockUo
import com.onepiece.gpgaming.beans.value.database.WithdrawCo
import com.onepiece.gpgaming.beans.value.database.WithdrawQuery
import com.onepiece.gpgaming.beans.value.database.WithdrawReportVo
import com.onepiece.gpgaming.beans.value.internet.web.WithdrawValue
import java.math.BigDecimal
import java.time.LocalDate

interface WithdrawService {

    fun findWithdraw(clientId: Int, orderId: String): Withdraw

    fun query(withdrawQuery: WithdrawQuery): List<Withdraw>

    fun query(withdrawQuery: WithdrawQuery, current: Int, size: Int): Page<Withdraw>

    fun create(withdrawCo: WithdrawCo)

    fun lock(withdrawLockUo: DepositLockUo)

    fun check(withdrawUoReq: WithdrawValue.WithdrawUoReq)

    fun report(startDate: LocalDate, endDate: LocalDate): List<WithdrawReportVo>

    fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientWithdrawReportVo>

    fun getTotalWithdraw(clientId: Int, memberId: Int, startDate: LocalDate): BigDecimal

}
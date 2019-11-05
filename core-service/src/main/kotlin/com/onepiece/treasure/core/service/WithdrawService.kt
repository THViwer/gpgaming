package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.model.Withdraw
import com.onepiece.treasure.beans.value.database.DepositLockUo
import com.onepiece.treasure.beans.value.database.WithdrawCo
import com.onepiece.treasure.beans.value.database.WithdrawQuery
import com.onepiece.treasure.beans.value.internet.web.ClientWithdrawReportVo
import com.onepiece.treasure.beans.value.internet.web.WithdrawReportVo
import com.onepiece.treasure.beans.value.internet.web.WithdrawUoReq
import java.time.LocalDate

interface WithdrawService {

    fun findWithdraw(clientId: Int, orderId: String): Withdraw

    fun query(withdrawQuery: WithdrawQuery): List<Withdraw>

    fun query(withdrawQuery: WithdrawQuery, current: Int, size: Int): Page<Withdraw>

    fun create(withdrawCo: WithdrawCo)

    fun lock(withdrawLockUo: DepositLockUo)

    fun check(withdrawUoReq: WithdrawUoReq)

    fun report(startDate: LocalDate, endDate: LocalDate): List<WithdrawReportVo>

    fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientWithdrawReportVo>

}
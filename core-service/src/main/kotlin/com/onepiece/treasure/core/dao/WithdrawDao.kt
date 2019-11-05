package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.Withdraw
import com.onepiece.treasure.beans.value.database.DepositLockUo
import com.onepiece.treasure.beans.value.database.WithdrawCo
import com.onepiece.treasure.beans.value.database.WithdrawQuery
import com.onepiece.treasure.beans.value.database.WithdrawUo
import com.onepiece.treasure.beans.value.internet.web.ClientWithdrawReportVo
import com.onepiece.treasure.beans.value.internet.web.DepositReportVo
import com.onepiece.treasure.beans.value.internet.web.WithdrawReportVo
import com.onepiece.treasure.core.dao.basic.BasicDao
import java.time.LocalDate

interface WithdrawDao: BasicDao<Withdraw> {

    fun findWithdraw(clientId: Int, orderId: String): Withdraw

    fun query(query: WithdrawQuery, current: Int, size: Int): List<Withdraw>

    fun total(query: WithdrawQuery): Int

    fun create(orderCo: WithdrawCo): Boolean

    fun lock(withdrawLockUo: DepositLockUo): Boolean

    fun check(orderUo: WithdrawUo): Boolean

    fun report(startDate: LocalDate, endDate: LocalDate): List<WithdrawReportVo>

    fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientWithdrawReportVo>

}
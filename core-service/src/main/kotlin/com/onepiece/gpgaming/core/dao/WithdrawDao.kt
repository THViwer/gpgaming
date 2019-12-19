package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Withdraw
import com.onepiece.gpgaming.beans.value.database.DepositLockUo
import com.onepiece.gpgaming.beans.value.database.WithdrawCo
import com.onepiece.gpgaming.beans.value.database.WithdrawQuery
import com.onepiece.gpgaming.beans.value.database.WithdrawUo
import com.onepiece.gpgaming.beans.value.database.ClientWithdrawReportVo
import com.onepiece.gpgaming.beans.value.database.WithdrawReportVo
import com.onepiece.gpgaming.core.dao.basic.BasicDao
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
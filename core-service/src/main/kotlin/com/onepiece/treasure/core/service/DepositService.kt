package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.model.Deposit
import com.onepiece.treasure.beans.value.database.DepositCo
import com.onepiece.treasure.beans.value.database.DepositLockUo
import com.onepiece.treasure.beans.value.database.DepositQuery
import com.onepiece.treasure.beans.value.database.ClientDepositReportVo
import com.onepiece.treasure.beans.value.database.DepositReportVo
import com.onepiece.treasure.beans.value.internet.web.DepositUoReq
import java.time.LocalDate

interface DepositService {

    fun findDeposit(clientId: Int, orderId: String): Deposit

    fun query(depositQuery: DepositQuery): List<Deposit>

    fun query(depositQuery: DepositQuery, current: Int, size: Int): Page<Deposit>

    fun create(depositCo: DepositCo)

    fun lock(depositLockUo: DepositLockUo)

    fun check(depositUoReq: DepositUoReq)

    fun report(startDate: LocalDate, endDate: LocalDate): List<DepositReportVo>

    fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientDepositReportVo>


}
package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.model.Deposit
import com.onepiece.gpgaming.beans.value.database.ClientDepositReportVo
import com.onepiece.gpgaming.beans.value.database.DepositCo
import com.onepiece.gpgaming.beans.value.database.DepositLockUo
import com.onepiece.gpgaming.beans.value.database.DepositQuery
import com.onepiece.gpgaming.beans.value.database.DepositReportVo
import com.onepiece.gpgaming.beans.value.database.FirstDepositVo
import com.onepiece.gpgaming.beans.value.internet.web.DepositValue
import java.math.BigDecimal
import java.time.LocalDate

interface DepositService {

    fun findDeposit(clientId: Int, orderId: String): Deposit

    fun query(depositQuery: DepositQuery): List<Deposit>

    fun query(depositQuery: DepositQuery, current: Int, size: Int): Page<Deposit>

    fun create(depositCo: DepositCo)

    fun lock(depositLockUo: DepositLockUo)

    fun check(depositUoReq: DepositValue.DepositUoReq)

    fun report(startDate: LocalDate, endDate: LocalDate): List<DepositReportVo>

    fun sumSuccessful(clientId: Int, memberId: Int, startDate: LocalDate, endDate: LocalDate): BigDecimal

    fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientDepositReportVo>

    fun queryFirstDepositDetail(startDate: LocalDate): List<FirstDepositVo>


}
package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Deposit
import com.onepiece.gpgaming.beans.value.database.ClientDepositReportVo
import com.onepiece.gpgaming.beans.value.database.DepositCo
import com.onepiece.gpgaming.beans.value.database.DepositLockUo
import com.onepiece.gpgaming.beans.value.database.DepositQuery
import com.onepiece.gpgaming.beans.value.database.DepositReportVo
import com.onepiece.gpgaming.beans.value.database.DepositUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao
import java.time.LocalDate

interface DepositDao: BasicDao<Deposit> {

    fun findDeposit(clientId: Int, orderId: String): Deposit

    fun query(query: DepositQuery, current: Int, size: Int): List<Deposit>

    fun total(query: DepositQuery): Int

    fun create(depositCo: DepositCo): Boolean

    fun lock(depositLockUo: DepositLockUo): Boolean

    fun update(depositUo: DepositUo): Boolean

    fun report(clientId: Int?, memberId: Int?, startDate: LocalDate, endDate: LocalDate): List<DepositReportVo>

    fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientDepositReportVo>

}
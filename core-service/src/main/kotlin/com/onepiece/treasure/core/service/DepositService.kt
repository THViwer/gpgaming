package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Deposit
import com.onepiece.treasure.beans.value.database.DepositCo
import com.onepiece.treasure.beans.value.database.DepositLockUo
import com.onepiece.treasure.beans.value.database.DepositQuery
import com.onepiece.treasure.beans.value.database.DepositUo

interface DepositService {

    fun findDeposit(clientId: Int, orderId: String): Deposit

    fun query(depositQuery: DepositQuery): List<Deposit>

    fun create(depositCo: DepositCo)

    fun lock(depositLockUo: DepositLockUo)

    fun update(depositUo: DepositUo)

}
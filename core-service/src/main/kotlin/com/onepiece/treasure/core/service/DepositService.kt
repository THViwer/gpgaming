package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Deposit
import com.onepiece.treasure.beans.value.database.DepositCo
import com.onepiece.treasure.beans.value.database.DepositQuery
import com.onepiece.treasure.beans.value.database.DepositUo

interface DepositService {

    fun query(depositQuery: DepositQuery): List<Deposit>

    fun create(depositCo: DepositCo)

    fun update(depositUo: DepositUo)

}
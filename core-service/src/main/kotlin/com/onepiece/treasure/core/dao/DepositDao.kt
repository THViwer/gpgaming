package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.Deposit
import com.onepiece.treasure.beans.value.database.DepositCo
import com.onepiece.treasure.beans.value.database.DepositLockUo
import com.onepiece.treasure.beans.value.database.DepositQuery
import com.onepiece.treasure.beans.value.database.DepositUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface DepositDao: BasicDao<Deposit> {

    fun findDeposit(clientId: Int, orderId: String): Deposit

    fun query(query: DepositQuery, current: Int, size: Int): List<Deposit>

    fun total(query: DepositQuery): Int

    fun create(depositCo: DepositCo): Boolean

    fun lock(depositLockUo: DepositLockUo): Boolean

    fun update(depositUo: DepositUo): Boolean

}
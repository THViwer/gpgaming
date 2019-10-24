package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.DepositCo
import com.onepiece.treasure.core.dao.value.DepositQuery
import com.onepiece.treasure.core.dao.value.DepositUo
import com.onepiece.treasure.core.model.Deposit

interface DepositDao: BasicDao<Deposit> {

    fun query(query: DepositQuery): List<Deposit>

    fun create(depositCo: DepositCo): Boolean

    fun update(depositUo: DepositUo): Boolean

}
package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.DepositCo
import com.onepiece.treasure.beans.value.database.DepositQuery
import com.onepiece.treasure.beans.value.database.DepositUo
import com.onepiece.treasure.beans.model.Deposit

interface DepositDao: BasicDao<Deposit> {

    fun query(query: DepositQuery): List<Deposit>

    fun create(depositCo: DepositCo): Boolean

    fun update(depositUo: DepositUo): Boolean

}
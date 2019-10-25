package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.BalanceCo
import com.onepiece.treasure.beans.value.database.BalanceUo
import com.onepiece.treasure.beans.model.Balance

interface BalanceDao: BasicDao<Balance> {

    fun create(balanceCo: BalanceCo): Boolean

    fun update(balanceUo: BalanceUo): Boolean

}
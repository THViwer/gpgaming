package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.BalanceCo
import com.onepiece.treasure.core.dao.value.BalanceUo
import com.onepiece.treasure.core.model.Balance

interface BalanceDao: BasicDao<Balance> {

    fun create(balanceCo: BalanceCo): Boolean

    fun update(balanceUo: BalanceUo): Boolean

}
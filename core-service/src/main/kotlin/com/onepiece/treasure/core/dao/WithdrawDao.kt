package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.model.Withdraw
import com.onepiece.treasure.beans.value.database.WithdrawOrderCo
import com.onepiece.treasure.beans.value.database.WithdrawOrderUo
import com.onepiece.treasure.beans.value.database.WithdrawQuery

interface WithdrawDao: BasicDao<Withdraw> {

    fun query(query: WithdrawQuery): List<Withdraw>

    fun create(orderCo: WithdrawOrderCo): Boolean

    fun update(orderUo: WithdrawOrderUo): Boolean

}
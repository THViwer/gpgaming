package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.*
import com.onepiece.treasure.core.model.Withdraw

interface WithdrawDao: BasicDao<Withdraw> {

    fun query(query: WithdrawQuery): List<Withdraw>

    fun create(orderCo: WithdrawOrderCo): Boolean

    fun update(orderUo: WithdrawOrderUo): Boolean

}
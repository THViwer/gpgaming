package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.*
import com.onepiece.treasure.core.model.TopUpOrder
import com.onepiece.treasure.core.model.WithdrawOrder

interface WithdrawOrderDao: BasicDao<WithdrawOrder> {

    fun query(query: TopUpOrderQuery): List<WithdrawOrder>

    fun create(orderCo: WithdrawOrderCo): Boolean

    fun update(orderUo: WithdrawOrderUo): Boolean

}
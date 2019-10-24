package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.TopUpOrderCo
import com.onepiece.treasure.core.dao.value.TopUpOrderQuery
import com.onepiece.treasure.core.dao.value.TopUpOrderUo
import com.onepiece.treasure.core.model.TopUpOrder

interface TopUpOrderDao: BasicDao<TopUpOrder> {

    fun query(query: TopUpOrderQuery): List<TopUpOrder>

    fun create(orderCo: TopUpOrderCo): Boolean

    fun update(orderUo: TopUpOrderUo): Boolean

}
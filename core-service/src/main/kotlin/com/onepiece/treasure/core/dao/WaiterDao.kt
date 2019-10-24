package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicQueryDao
import com.onepiece.treasure.core.dao.value.WaiterCo
import com.onepiece.treasure.core.dao.value.WaiterUo
import com.onepiece.treasure.core.model.Waiter

interface WaiterDao : BasicQueryDao<Waiter> {

    fun create(waiterCo: WaiterCo): Boolean

    fun update(waiterUo: WaiterUo): Boolean

}
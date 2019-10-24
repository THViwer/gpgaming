package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.WaiterCo
import com.onepiece.treasure.core.dao.value.WaiterUo
import com.onepiece.treasure.core.model.Waiter

interface WaiterDao : BasicDao<Waiter> {

    fun create(waiterCo: WaiterCo): Boolean

    fun update(waiterUo: WaiterUo): Boolean

}
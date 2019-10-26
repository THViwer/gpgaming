package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.WaiterCo
import com.onepiece.treasure.beans.value.database.WaiterUo
import com.onepiece.treasure.beans.model.Waiter

interface WaiterDao : BasicDao<Waiter> {

    fun findByUsername(username: String): Waiter?

    fun create(waiterCo: WaiterCo): Int

    fun update(waiterUo: WaiterUo): Boolean

}
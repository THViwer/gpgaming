package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Waiter
import com.onepiece.gpgaming.beans.value.database.WaiterValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface WaiterDao : BasicDao<Waiter> {

    fun findByUsername(username: String): Waiter?

    fun create(waiterCo: WaiterValue.WaiterCo): Int

    fun update(waiterUo: WaiterValue.WaiterUo): Boolean

}
package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Waiter
import com.onepiece.gpgaming.beans.value.database.WaiterCo
import com.onepiece.gpgaming.beans.value.database.WaiterUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface WaiterDao : BasicDao<Waiter> {

    fun findByUsername(username: String): Waiter?

    fun create(waiterCo: WaiterCo): Int

    fun update(waiterUo: WaiterUo): Boolean

}
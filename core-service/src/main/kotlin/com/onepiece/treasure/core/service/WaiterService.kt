package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Waiter
import com.onepiece.treasure.beans.value.database.LoginValue
import com.onepiece.treasure.beans.value.database.WaiterCo
import com.onepiece.treasure.beans.value.database.WaiterUo

interface WaiterService {

    fun get(id: Int): Waiter

    fun findClientWaiters(clientId: Int): List<Waiter>

    fun login(loginValue: LoginValue): Waiter

    fun create(waiterCo: WaiterCo)

    fun update(waiterUo: WaiterUo)

}
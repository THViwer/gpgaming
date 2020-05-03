package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.Waiter
import com.onepiece.gpgaming.beans.value.database.ClientLoginValue
import com.onepiece.gpgaming.beans.value.database.WaiterCo
import com.onepiece.gpgaming.beans.value.database.WaiterUo

interface WaiterService {

    fun get(id: Int): Waiter

    fun findClientWaiters(clientId: Int): List<Waiter>

    fun login(loginValue: ClientLoginValue): Waiter

    fun create(waiterCo: WaiterCo)

    fun findByUsername(clientId: Int, username: String?): Waiter?

    fun update(waiterUo: WaiterUo)

}
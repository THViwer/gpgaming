package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.Waiter
import com.onepiece.gpgaming.beans.value.database.ClientLoginValue
import com.onepiece.gpgaming.beans.value.database.WaiterValue

interface WaiterService {

    fun get(id: Int): Waiter

    fun findClientWaiters(clientId: Int): List<Waiter>

    fun login(loginValue: ClientLoginValue.ClientLoginReq): Waiter

    fun create(waiterCo: WaiterValue.WaiterCo)

    fun findByUsername(clientId: Int, username: String?): Waiter?

    fun update(waiterUo: WaiterValue.WaiterUo)

    fun selectSale(bossId: Int, clientId: Int, saleId: Int?): Waiter?

}
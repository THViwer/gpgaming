package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Client
import com.onepiece.treasure.beans.value.database.ClientCo
import com.onepiece.treasure.beans.value.database.ClientUo
import com.onepiece.treasure.beans.value.database.LoginValue
import java.math.BigDecimal

interface ClientService {

    fun all(): List<Client>

    fun login(loginValue: LoginValue): Client

    fun create(clientCo: ClientCo)

    fun update(clientUo: ClientUo)

//    fun updateEarnestBalance(id: Int, earnestBalance: BigDecimal)

}
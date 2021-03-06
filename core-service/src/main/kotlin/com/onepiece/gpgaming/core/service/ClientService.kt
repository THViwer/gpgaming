package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.Client
import com.onepiece.gpgaming.beans.value.database.ClientCo
import com.onepiece.gpgaming.beans.value.database.ClientLoginValue
import com.onepiece.gpgaming.beans.value.database.ClientUo

interface ClientService {

    fun getMainClient(bossId: Int): Client?

    fun get(id: Int): Client

    fun all(): List<Client>

    fun login(loginValue: ClientLoginValue.ClientLoginReq): Client

    fun create(clientCo: ClientCo)

    fun update(clientUo: ClientUo)

    fun checkPassword(id: Int, password: String): Boolean

//    fun updateEarnestBalance(id: Int, earnestBalance: BigDecimal)

}
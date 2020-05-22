package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.Client
import com.onepiece.gpgaming.beans.value.database.ClientCo
import com.onepiece.gpgaming.beans.value.database.ClientLoginValue
import com.onepiece.gpgaming.beans.value.database.ClientUo
import com.onepiece.gpgaming.beans.value.database.LoginValue
import java.math.BigDecimal

interface ClientService {

    fun getMainClient(bossId: Int): Client?

    fun get(id: Int): Client

    fun all(): List<Client>

    fun login(loginValue: ClientLoginValue): Client

    fun create(clientCo: ClientCo)

    fun update(clientUo: ClientUo)

//    fun updateEarnestBalance(id: Int, earnestBalance: BigDecimal)

}
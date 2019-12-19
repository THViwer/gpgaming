package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Client
import com.onepiece.gpgaming.beans.value.database.ClientCo
import com.onepiece.gpgaming.beans.value.database.ClientUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface ClientDao : BasicDao<Client> {

    fun findByUsername(username: String): Client?

    fun create(clientCo: ClientCo): Int

    fun update(clientUo: ClientUo): Boolean

//    fun updateEarnestBalance(id: Int, earnestBalance: BigDecimal, processId: String): Boolean



}
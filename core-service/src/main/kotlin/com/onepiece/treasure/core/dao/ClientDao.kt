package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.ClientCo
import com.onepiece.treasure.beans.value.database.ClientUo
import com.onepiece.treasure.beans.model.Client
import java.math.BigDecimal

interface ClientDao : BasicDao<Client> {

    fun findByUsername(username: String): Client?

    fun create(clientCo: ClientCo): Int

    fun update(clientUo: ClientUo): Boolean

    fun updateEarnestBalance(id: Int, earnestBalance: BigDecimal, processId: String): Boolean



}
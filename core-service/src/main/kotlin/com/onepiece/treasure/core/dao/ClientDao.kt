package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.ClientCo
import com.onepiece.treasure.beans.value.database.ClientUo
import com.onepiece.treasure.beans.model.Client

interface ClientDao : BasicDao<Client> {

    fun findByUsername(username: String): Client?

    fun create(clientCo: ClientCo): Int

    fun update(clientUo: ClientUo): Boolean

}
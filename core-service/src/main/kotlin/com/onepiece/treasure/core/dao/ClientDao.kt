package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.ClientCo
import com.onepiece.treasure.core.dao.value.ClientUo
import com.onepiece.treasure.core.model.Client

interface ClientDao : BasicDao<Client> {

    fun create(clientCo: ClientCo): Boolean

    fun update(clientUo: ClientUo): Boolean

}
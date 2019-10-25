package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.ClientCo
import com.onepiece.treasure.beans.value.database.ClientUo
import com.onepiece.treasure.beans.model.Client

interface ClientDao : BasicDao<Client> {

    fun create(clientCo: ClientCo): Boolean

    fun update(clientUo: ClientUo): Boolean

}
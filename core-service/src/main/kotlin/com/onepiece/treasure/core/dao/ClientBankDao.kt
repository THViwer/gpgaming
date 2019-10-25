package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.ClientBankCo
import com.onepiece.treasure.beans.value.database.ClientBankUo
import com.onepiece.treasure.beans.model.ClientBank

interface ClientBankDao : BasicDao<ClientBank> {

    fun findClientBank(clientId: Int): List<ClientBank>

    fun create(clientBankCo: ClientBankCo): Boolean

    fun update(clientBankUo: ClientBankUo): Boolean

}
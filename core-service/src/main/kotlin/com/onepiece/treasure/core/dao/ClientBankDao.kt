package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicQueryDao
import com.onepiece.treasure.core.dao.value.ClientBankCo
import com.onepiece.treasure.core.dao.value.ClientBankUo
import com.onepiece.treasure.core.model.ClientBank

interface ClientBankDao : BasicQueryDao<ClientBank> {

    fun create(clientBankCo: ClientBankCo): Boolean

    fun update(clientBankUo: ClientBankUo): Boolean

}
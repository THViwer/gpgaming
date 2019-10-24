package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.ClientBankCo
import com.onepiece.treasure.core.dao.value.ClientBankUo
import com.onepiece.treasure.core.model.ClientBank

interface ClientBankDao : BasicDao<ClientBank> {

    fun create(clientBankCo: ClientBankCo): Boolean

    fun update(clientBankUo: ClientBankUo): Boolean

}
package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.ClientBank
import com.onepiece.gpgaming.beans.value.database.ClientBankCo
import com.onepiece.gpgaming.beans.value.database.ClientBankUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface ClientBankDao : BasicDao<ClientBank> {

    fun findClientBank(clientId: Int): List<ClientBank>

    fun create(clientBankCo: ClientBankCo): Boolean

    fun update(clientBankUo: ClientBankUo): Boolean

}
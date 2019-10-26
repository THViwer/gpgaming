package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.ClientBank
import com.onepiece.treasure.beans.value.database.ClientBankCo
import com.onepiece.treasure.beans.value.database.ClientBankUo

interface ClientBankService {

    fun get(id: Int): ClientBank

    fun findClientBank(clientId: Int): List<ClientBank>

    fun create(clientBankCo: ClientBankCo)

    fun update(clientBankUo: ClientBankUo)

}
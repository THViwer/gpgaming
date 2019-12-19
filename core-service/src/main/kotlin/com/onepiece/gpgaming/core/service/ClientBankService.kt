package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.ClientBank
import com.onepiece.gpgaming.beans.value.database.ClientBankCo
import com.onepiece.gpgaming.beans.value.database.ClientBankUo

interface ClientBankService {

    fun get(id: Int): ClientBank

    fun findClientBank(clientId: Int): List<ClientBank>

    fun create(clientBankCo: ClientBankCo)

    fun update(clientBankUo: ClientBankUo)

}
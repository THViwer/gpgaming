package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.ClientBankDao
import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.ClientBankCo
import com.onepiece.treasure.core.dao.value.ClientBankUo
import com.onepiece.treasure.core.model.ClientBank
import com.onepiece.treasure.core.model.enums.Status
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ClientBankDaoImpl : BasicDao<ClientBank>("client_bank"), ClientBankDao {

    override fun mapper(): (rs: ResultSet) -> ClientBank {
        return { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val bankCardNumber = rs.getString("bank_card_number")
            val cardName = rs.getString("card_name")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            ClientBank(id = id, clientId = clientId, bankCardNumber = bankCardNumber, cardName = cardName, status = status,
                    createdTime = createdTime)
        }
    }

    override fun create(clientBankCo: ClientBankCo): Boolean {
        return insert().set("client_id", clientBankCo.clientId)
                .set("bank_card_number", clientBankCo.bankCardNumber)
                .set("card_name", clientBankCo.cardName)
                .set("status", Status.Normal)
                .executeOnlyOne()
    }

    override fun update(clientBankUo: ClientBankUo): Boolean {
        return update().set("bank_card_number", clientBankUo.bankCardNumber)
                .set("card_name", clientBankUo.cardName)
                .set("status", clientBankUo.status)
                .where("id", clientBankUo.id)
                .executeOnlyOne()
    }
}
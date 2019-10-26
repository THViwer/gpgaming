package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Bank
import com.onepiece.treasure.core.dao.ClientBankDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.beans.value.database.ClientBankCo
import com.onepiece.treasure.beans.value.database.ClientBankUo
import com.onepiece.treasure.beans.model.ClientBank
import com.onepiece.treasure.beans.enums.Status
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ClientBankDaoImpl : BasicDaoImpl<ClientBank>("client_bank"), ClientBankDao {
    
    override val mapper: (rs: ResultSet) -> ClientBank
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val bank = rs.getString("bank").let { Bank.valueOf(it) }
            val bankCardNumber = rs.getString("bank_card_number")
            val name = rs.getString("name")
            val levelId = rs.getInt("level_id")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            ClientBank(id = id, clientId = clientId, bank = bank, bankCardNumber = bankCardNumber, name = name, status = status,
                    createdTime = createdTime, levelId = levelId)
        }

    override fun findClientBank(clientId: Int): List<ClientBank> {
        return query().where("client_id", clientId)
                .execute(mapper)
    }

    override fun create(clientBankCo: ClientBankCo): Boolean {
        return insert().set("client_id", clientBankCo.clientId)
                .set("bank", clientBankCo.bank)
                .set("bank_card_number", clientBankCo.bankCardNumber)
                .set("name", clientBankCo.name)
                .set("level_id", clientBankCo.levelId)
                .set("status", Status.Normal)
                .executeOnlyOne()
    }

    override fun update(clientBankUo: ClientBankUo): Boolean {
        return update().set("bank_card_number", clientBankUo.bankCardNumber)
                .set("bank", clientBankUo.bank)
                .set("name", clientBankUo.name)
                .set("bank_card_number", clientBankUo.bankCardNumber)
                .set("status", clientBankUo.status)
                .set("level_id", clientBankUo.levelId)
                .where("id", clientBankUo.id)
                .executeOnlyOne()
    }
}
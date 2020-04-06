package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.ClientBank
import com.onepiece.gpgaming.beans.value.database.ClientBankCo
import com.onepiece.gpgaming.beans.value.database.ClientBankUo
import com.onepiece.gpgaming.core.dao.ClientBankDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import kotlin.math.max

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
            val minAmount = rs.getBigDecimal("min_amount")
            val maxAmount = rs.getBigDecimal("max_amount")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            ClientBank(id = id, clientId = clientId, bank = bank, bankCardNumber = bankCardNumber, name = name, status = status,
                    createdTime = createdTime, levelId = levelId, minAmount = minAmount, maxAmount = maxAmount)
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
                .set("min_amount", clientBankCo.minAmount)
                .set("max_amount", clientBankCo.maxAmount)
                .executeOnlyOne()
    }

    override fun update(clientBankUo: ClientBankUo): Boolean {
        return update().set("bank_card_number", clientBankUo.bankCardNumber)
                .set("bank", clientBankUo.bank)
                .set("name", clientBankUo.name)
                .set("bank_card_number", clientBankUo.bankCardNumber)
                .set("status", clientBankUo.status)
                .setIfNull("level_id", clientBankUo.levelId)
                .set("min_amount", clientBankUo.minAmount)
                .set("max_amount", clientBankUo.maxAmount)
                .where("id", clientBankUo.id)
                .executeOnlyOne()
    }
}
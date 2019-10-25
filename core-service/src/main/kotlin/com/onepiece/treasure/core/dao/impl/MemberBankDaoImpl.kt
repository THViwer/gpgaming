package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.MemberBankDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.beans.value.database.MemberBankCo
import com.onepiece.treasure.beans.value.database.MemberBankUo
import com.onepiece.treasure.beans.model.MemberBank
import com.onepiece.treasure.beans.enums.Banks
import com.onepiece.treasure.beans.enums.Status
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class MemberBankDaoImpl : BasicDaoImpl<MemberBank>("member_bank"), MemberBankDao {

    override fun mapper(): (rs: ResultSet) -> MemberBank {
        return { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val bank = rs.getString("bank").let { Banks.valueOf(it) }
            val name = rs.getString("name")
            val bankCardNumber = rs.getString("bank_card_number")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            MemberBank(id = id, clientId = clientId, memberId = memberId, bank = bank, name = name, bankCardNumber = bankCardNumber,
                    status = status, createdTime = createdTime)
        }
    }

    override fun create(memberBankCo: MemberBankCo): Boolean {
        return insert().set("client_id", memberBankCo.clientId)
                .set("member_id", memberBankCo.memberId)
                .set("bank", memberBankCo.bank)
                .set("name", memberBankCo.name)
                .set("bank_card_number", memberBankCo.bankCardNumber)
                .executeOnlyOne()

    }

    override fun update(memberBankUo: MemberBankUo): Boolean {
        return update().set("bank", memberBankUo.bank)
                .set("name", memberBankUo.name)
                .set("bank_card_number", memberBankUo.bankCardNumber)
                .where("id", memberBankUo.id)
                .executeOnlyOne()
    }
}
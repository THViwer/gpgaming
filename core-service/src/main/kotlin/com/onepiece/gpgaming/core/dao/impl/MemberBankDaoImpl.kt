package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.MemberBank
import com.onepiece.gpgaming.beans.value.database.MemberBankCo
import com.onepiece.gpgaming.beans.value.database.MemberBankUo
import com.onepiece.gpgaming.core.dao.MemberBankDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class MemberBankDaoImpl : BasicDaoImpl<MemberBank>("member_bank"), MemberBankDao {

    override val mapper: (rs: ResultSet) -> MemberBank
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val bank = rs.getString("bank").let { Bank.valueOf(it) }
            val bankCardNumber = rs.getString("bank_card_number")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            MemberBank(id = id, clientId = clientId, memberId = memberId, bank = bank, bankCardNumber = bankCardNumber,
                    status = status, createdTime = createdTime)
        }

    override fun query(memberId: Int): List<MemberBank> {
        return query().where("member_id", memberId)
                .execute(mapper)

    }

    override fun create(memberBankCo: MemberBankCo): Int {
        return insert().set("client_id", memberBankCo.clientId)
                .set("member_id", memberBankCo.memberId)
                .set("bank", memberBankCo.bank)
                .set("bank_card_number", memberBankCo.bankCardNumber)
                .set("status", Status.Normal)
                .executeGeneratedKey()

    }

    override fun update(memberBankUo: MemberBankUo): Boolean {
        return update().set("bank", memberBankUo.bank)
                .set("bank_card_number", memberBankUo.bankCardNumber)
                .where("id", memberBankUo.id)
                .executeOnlyOne()
    }
}
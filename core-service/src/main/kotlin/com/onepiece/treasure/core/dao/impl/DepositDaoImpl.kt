package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Bank
import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.model.Deposit
import com.onepiece.treasure.beans.value.database.DepositCo
import com.onepiece.treasure.beans.value.database.DepositQuery
import com.onepiece.treasure.beans.value.database.DepositUo
import com.onepiece.treasure.core.dao.DepositDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class DepositDaoImpl : BasicDaoImpl<Deposit>("deposit"), DepositDao {

    override val mapper: (rs: ResultSet) -> Deposit
        get() = { rs ->
            val id = rs.getInt("id")
            val orderId = rs.getString("order_id")
            val processId = rs.getString("process_id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val name = rs.getString("name")
            val bank = rs.getString("bank").let { Bank.valueOf(it) }
            val bankCardNumber = rs.getString("bank_card_number")
            val clientBankId = rs.getInt("client_bank_id")
            val clientBankCardNumber = rs.getString("client_bank_card_number")
            val clientBankName = rs.getString("client_bank_name")
            val money = rs.getBigDecimal("money")
            val imgPath = rs.getString("imgPath")
            val state = rs.getString("state").let { DepositState.valueOf(it) }
            val remarks = rs.getString("remarks")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val endTime = rs.getTimestamp("end_time")?.toLocalDateTime()
            Deposit(id = id, orderId = orderId, clientId = clientId, memberId = memberId, bank = bank, money = money,
                    imgPath = imgPath, state = state, remarks = remarks, createdTime = createdTime, endTime = endTime,
                    bankCardNumber = bankCardNumber, processId = processId, name = name, clientBankId = clientBankId,
                    clientBankName = clientBankName, clientBankCardNumber = clientBankCardNumber)
        }

    override fun query(query: DepositQuery): List<Deposit> {
        return query().where("client_id", query.clientId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .where("member_id", query.memberId)
                .where("order_id", query.orderId)
                .where("state", query.state)
                .limit(0, 1000)
                .execute(mapper)
    }

    override fun create(depositCo: DepositCo): Boolean {
        return insert().set("order_id", depositCo.orderId)
                .set("process_id", UUID.randomUUID().toString())
                .set("client_id", depositCo.clientId)
                .set("member_id", depositCo.memberId)
                .set("name", depositCo.name)
                .set("bank", depositCo.bank)
                .set("bank_card_number", depositCo.bankCardNumber)
                .set("client_bank_id", depositCo.clientBankId)
                .set("client_bank_name", depositCo.clientBankName)
                .set("client_bank_card_number", depositCo.clientBankCardNumber)
                .set("money", depositCo.money)
                .set("img_path", depositCo.imgPath)
                .set("state", DepositState.Process)
                .executeOnlyOne()
    }

    override fun update(depositUo: DepositUo): Boolean {
        val sql = "update deposit set state = ?, remarks = ?, process_id = ? where order_id = ? and process_id = ?"
        return jdbcTemplate.update(sql, depositUo.state.name, depositUo.remarks, UUID.randomUUID().toString(), depositUo.orderId, depositUo.processId) == 1
    }
}
package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.DepositDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.beans.model.Deposit
import com.onepiece.treasure.beans.enums.Banks
import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.value.database.DepositCo
import com.onepiece.treasure.beans.value.database.DepositQuery
import com.onepiece.treasure.beans.value.database.DepositUo
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class DepositDaoImpl : BasicDaoImpl<Deposit>("deposit"), DepositDao {

    override fun mapper(): (rs: ResultSet) -> Deposit {
        return { rs ->
            val id = rs.getInt("id")
            val orderId = rs.getString("order_id")
            val processId = rs.getString("process_id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val bank = rs.getString("bank").let { Banks.valueOf(it) }
            val bankCardNumber = rs.getString("bank_card_number")
            val money = rs.getBigDecimal("money")
            val imgPath = rs.getString("imgPath")
            val state = rs.getString("state").let { DepositState.valueOf(it) }
            val remarks = rs.getString("remarks")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val endTime = rs.getTimestamp("end_time")?.toLocalDateTime()
            Deposit(id = id, orderId = orderId, clientId = clientId, memberId = memberId, bank = bank, money = money,
                    imgPath = imgPath, state = state, remarks = remarks, createdTime = createdTime, endTime = endTime,
                    bankCardNumber = bankCardNumber, processId = processId)
        }
    }

    override fun query(query: DepositQuery): List<Deposit> {
        return query().where("client_id", query.clientId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .where("member_id", query.memberId)
                .where("order_id", query.orderId)
                .where("state", query.state)
                .execute(mapper())
    }

    override fun create(depositCo: DepositCo): Boolean {
        return insert().set("order_id", depositCo.orderId)
                .set("process_id", UUID.randomUUID().toString())
                .set("client_id", depositCo.clientId)
                .set("member_id", depositCo.memberId)
                .set("bank", depositCo.bank)
                .set("bank_card_number", depositCo.bankCardNumber)
                .set("money", depositCo.money)
                .set("imgPath", depositCo.imgPath)
                .set("state", DepositState.Process)
                .executeOnlyOne()
    }

    override fun update(depositUo: DepositUo): Boolean {
        val sql = "update deposit set state = ?, remarks = ?, process_id = ? where order_id = ? and process_id = ?"
        return jdbcTemplate.update(sql, depositUo.state.name, depositUo.remarks, UUID.randomUUID().toString(), depositUo.orderId, depositUo.processId) == 1
    }
}
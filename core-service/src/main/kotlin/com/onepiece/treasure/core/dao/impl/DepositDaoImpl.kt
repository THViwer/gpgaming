package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Bank
import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.model.Deposit
import com.onepiece.treasure.beans.value.database.DepositCo
import com.onepiece.treasure.beans.value.database.DepositLockUo
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
            val memberName = rs.getString("member_name")
            val memberBank = rs.getString("member_bank").let { Bank.valueOf(it) }
            val memberBankCardNumber = rs.getString("member_bank_card_number")
            val clientBankId = rs.getInt("client_bank_id")
            val clientBankCardNumber = rs.getString("client_bank_card_number")
            val clientBankName = rs.getString("client_bank_name")
            val money = rs.getBigDecimal("money")
            val imgPath = rs.getString("imgPath")
            val state = rs.getString("state").let { DepositState.valueOf(it) }
            val remarks = rs.getString("remarks")
            val lockWaiterId = rs.getInt("lock_waiter_id")
            val lockWaiterName = rs.getString("lock_waiter_name")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val endTime = rs.getTimestamp("end_time")?.toLocalDateTime()
            Deposit(id = id, orderId = orderId, clientId = clientId, memberId = memberId, memberBank = memberBank, money = money,
                    imgPath = imgPath, state = state, remarks = remarks, createdTime = createdTime, endTime = endTime,
                    memberBankCardNumber = memberBankCardNumber, processId = processId, memberName = memberName, clientBankId = clientBankId,
                    clientBankName = clientBankName, clientBankCardNumber = clientBankCardNumber, lockWaiterId = lockWaiterId,
                    lockWaiterName = lockWaiterName)
        }

    override fun findDeposit(clientId: Int, orderId: String): Deposit {
        return query().where("client_id", clientId)
                .where("order_id", orderId)
                .executeOnlyOne(mapper)
    }

    override fun query(query: DepositQuery, current: Int, size: Int): List<Deposit> {
        return query().where("client_id", query.clientId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .where("member_id", query.memberId)
                .where("order_id", query.orderId)
                .where("state", query.state)
                .limit(current, size)
                .execute(mapper)
    }

    override fun total(query: DepositQuery): Int {
        return query(" count(*) as count")
                .where("client_id", query.clientId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .where("member_id", query.memberId)
                .where("order_id", query.orderId)
                .where("state", query.state)
                .count()
    }

    override fun lock(depositLockUo: DepositLockUo): Boolean {
        return update().set("lock_waiter_id", depositLockUo.lockWaiterId)
                .set("lock_waiter_name", depositLockUo.lockWaiterName)
                .set("process_id", UUID.randomUUID().toString())
                .where("client_id", depositLockUo.clientId)
                .where("order_id", depositLockUo.orderId)
                .where("process_id", depositLockUo.processId)
                .asWhere("lock_waiter_id is null")
                .executeOnlyOne()
    }

    override fun create(depositCo: DepositCo): Boolean {
        return insert().set("order_id", depositCo.orderId)
                .set("process_id", UUID.randomUUID().toString())
                .set("client_id", depositCo.clientId)
                .set("member_id", depositCo.memberId)
                .set("member_name", depositCo.memberName)
                .set("member_bank", depositCo.memberBank)
                .set("member_bank_card_number", depositCo.memberBankCardNumber)
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
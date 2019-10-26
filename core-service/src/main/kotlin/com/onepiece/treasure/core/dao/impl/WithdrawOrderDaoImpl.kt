package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Bank
import com.onepiece.treasure.beans.enums.WithdrawState
import com.onepiece.treasure.beans.model.Withdraw
import com.onepiece.treasure.beans.value.database.DepositLockUo
import com.onepiece.treasure.beans.value.database.WithdrawCo
import com.onepiece.treasure.beans.value.database.WithdrawQuery
import com.onepiece.treasure.beans.value.database.WithdrawUo
import com.onepiece.treasure.core.dao.WithdrawDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class WithdrawOrderDaoImpl : BasicDaoImpl<Withdraw>("withdraw_order"), WithdrawDao {

    override val mapper: (rs: ResultSet) -> Withdraw
        get() = { rs ->
            val id = rs.getInt("id")
            val orderId = rs.getString("order_id")
            val processId = rs.getString("process_id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val memberBankId = rs.getInt("member_bank_id")
            val memberBank = rs.getString("member_bank").let { Bank.valueOf(it) }
            val memberName = rs.getString("member_name")
            val memberBankCardNumber = rs.getString("member_bank_card_number")
            val money = rs.getBigDecimal("money")
            val state = rs.getString("state").let { WithdrawState.valueOf(it) }
            val remarks = rs.getString("remarks")
            val lockWaiterId = rs.getInt("lock_waiter_id")
            val lockWaiterName = rs.getString("lock_waiter_name")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val endTime = rs.getTimestamp("end_time")?.toLocalDateTime()
            Withdraw(id = id, orderId = orderId, processId = processId, clientId = clientId, memberId = memberId,
                    memberBankId = memberBankId, money = money, state = state, remarks = remarks, createdTime = createdTime,
                    endTime = endTime, memberName = memberName, lockWaiterId = lockWaiterId, lockWaiterName = lockWaiterName,
                    memberBankCardNumber = memberBankCardNumber, memberBank = memberBank)
        }

    override fun findWithdraw(clientId: Int, orderId: String): Withdraw {
        return query().where("client_id", clientId)
                .where("order_id", orderId)
                .executeOnlyOne(mapper)
    }

    override fun query(query: WithdrawQuery, current: Int, size: Int): List<Withdraw> {
        return query().where("client_id", query.clientId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .where("order_id", query.orderId)
                .where("member_id", query.memberId)
                .where("state", query.state)
                .limit(current, size)
                .execute(mapper)
    }

    override fun total(query: WithdrawQuery): Int {
        return query(" count(*) as count").where("client_id", query.clientId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .where("order_id", query.orderId)
                .where("member_id", query.memberId)
                .where("state", query.state)
                .count()
    }

    override fun lock(withdraLockUo: DepositLockUo): Boolean {
        return update().set("lock_waiter_id", withdraLockUo.lockWaiterId)
                .set("lock_waiter_name", withdraLockUo.lockWaiterName)
                .set("process_id", UUID.randomUUID().toString())
                .where("client_id", withdraLockUo.clientId)
                .where("order_id", withdraLockUo.orderId)
                .where("process_id", withdraLockUo.processId)
                .asWhere("lock_waiter_id is null")
                .executeOnlyOne()
    }

    override fun create(orderCo: WithdrawCo): Boolean {
        return insert().set("order_id", orderCo.orderId)
                .set("process_id", UUID.randomUUID().toString())
                .set("client_id", orderCo.clientId)
                .set("member_id", orderCo.memberId)
                .set("member_bank_id", orderCo.memberBankId)
                .set("member_name", orderCo.memberBankId)
                .set("member_bank_card_number", orderCo.memberBankCardNumber)
                .set("money", orderCo.money)
                .set("state", WithdrawState.Process)
                .set("remarks", orderCo.remarks)
                .executeOnlyOne()
    }

    override fun update(orderUo: WithdrawUo): Boolean {
        val sql = "update withdraw_order set state = ? and remarks = ?, process_id = ? where order_id = ? and process_id = ?"
        return jdbcTemplate.update(sql, orderUo.state.name, orderUo.remarks, UUID.randomUUID().toString(), orderUo.orderId, orderUo.processId) == 1
    }
}
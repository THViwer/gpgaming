package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.WithdrawDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.core.dao.value.WithdrawOrderCo
import com.onepiece.treasure.core.dao.value.WithdrawOrderUo
import com.onepiece.treasure.core.dao.value.WithdrawQuery
import com.onepiece.treasure.core.model.Withdraw
import com.onepiece.treasure.core.model.enums.WithdrawState
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class WithdrawOrderDaoImpl : BasicDaoImpl<Withdraw>("withdraw_order"), WithdrawDao {

    override fun mapper(): (rs: ResultSet) -> Withdraw {
        return { rs ->
            val id = rs.getInt("id")
            val orderId = rs.getString("order_id")
            val processId = rs.getString("process_id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val memberBankId = rs.getInt("member_bank_id")
            val money = rs.getBigDecimal("money")
            val state = rs.getString("state").let { WithdrawState.valueOf(it) }
            val remarks = rs.getString("remarks")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val endTime = rs.getTimestamp("end_time")?.toLocalDateTime()
            Withdraw(id = id, orderId = orderId, processId = processId, clientId = clientId, memberId = memberId,
                    memberBankId = memberBankId, money = money, state = state, remarks = remarks, createdTime = createdTime,
                    endTime = endTime)
        }
    }

    override fun query(query: WithdrawQuery): List<Withdraw> {
        return query().where("client_id", query.clientId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_tiem <= ?", query.endTime)
                .where("order_id", query.orderId)
                .where("member_id", query.memberId)
                .where("state", query.state)
                .execute(mapper())

    }

    override fun create(orderCo: WithdrawOrderCo): Boolean {
        return insert().set("order_id", orderCo.orderId)
                .set("process_id", UUID.randomUUID().toString())
                .set("client_id", orderCo.clientId)
                .set("member_id", orderCo.memberId)
                .set("member_bank_id", orderCo.memberBankId)
                .set("money", orderCo.money)
                .set("state", WithdrawState.Process)
                .set("remarks", orderCo.remarks)
                .executeOnlyOne()
    }

    override fun update(orderUo: WithdrawOrderUo): Boolean {
        val sql = "update withdraw_order set state = ? and remarks = ?, process_id = ? where order_id = ? and process_id = ?"
        return jdbcTemplate.update(sql, orderUo.state.name, orderUo.remarks, UUID.randomUUID().toString(), orderUo.orderId, orderUo.processId) == 1
    }
}
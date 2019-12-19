package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.model.ArtificialOrder
import com.onepiece.gpgaming.beans.value.database.ArtificialOrderCo
import com.onepiece.gpgaming.beans.value.database.ArtificialOrderQuery
import com.onepiece.gpgaming.core.dao.ArtificialOrderDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ArtificialOrderDaoImpl : BasicDaoImpl<ArtificialOrder>("artificial_order"), ArtificialOrderDao {

    override val mapper: (rs: ResultSet) -> ArtificialOrder
        get() = { rs ->
            val id = rs.getInt("id")
            val orderId = rs.getString("order_id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val operatorId = rs.getInt("operator_id")
            val operatorRole = rs.getString("operator_role").let { Role.valueOf(it) }
            val balance = rs.getBigDecimal("balance")
            val beforeBalance = rs.getBigDecimal("before_balance")
            val remarks = rs.getString("remarks")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            ArtificialOrder(id = id, clientId = clientId, memberId = memberId, operatorId = operatorId, operatorRole = operatorRole,
                    balance = balance, beforeBalance = beforeBalance, remarks = remarks, createdTime = createdTime, orderId = orderId)
        }

    override fun query(query: ArtificialOrderQuery): List<ArtificialOrder> {
        return query()
                .where("client_id", query.clientId)
                .where("operator_role", query.operatorRole)
                .where("member_id", query.memberId)
                .limit(query.current, query.size)
                .execute(mapper)
    }

    override fun total(query: ArtificialOrderQuery): Int {
        return query()
                .where("client_id", query.clientId)
                .where("operator_role", query.operatorRole)
                .where("member_id", query.memberId)
                .count()

    }

    override fun create(artificialOrder: ArtificialOrderCo): Boolean {
        return insert()
                .set("order_id", artificialOrder.orderId)
                .set("client_id", artificialOrder.clientId)
                .set("member_id", artificialOrder.memberId)
                .set("operator_id", artificialOrder.operatorId)
                .set("operator_role", artificialOrder.operatorRole)
                .set("balance", artificialOrder.balance)
                .set("before_balance", artificialOrder.beforeBalance)
                .set("remarks", artificialOrder.remarks)
                .executeOnlyOne()
    }
}
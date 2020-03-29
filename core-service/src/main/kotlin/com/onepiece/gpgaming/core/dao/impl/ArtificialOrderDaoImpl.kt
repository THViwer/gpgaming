package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.ArtificialOrder
import com.onepiece.gpgaming.beans.value.database.ArtificialCReportVo
import com.onepiece.gpgaming.beans.value.database.ArtificialOrderCo
import com.onepiece.gpgaming.beans.value.database.ArtificialOrderQuery
import com.onepiece.gpgaming.beans.value.database.ArtificialReportVo
import com.onepiece.gpgaming.core.dao.ArtificialOrderDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class ArtificialOrderDaoImpl : BasicDaoImpl<ArtificialOrder>("artificial_order"), ArtificialOrderDao {

    override val mapper: (rs: ResultSet) -> ArtificialOrder
        get() = { rs ->
            val id = rs.getInt("id")
            val orderId = rs.getString("order_id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val username = rs.getString("username")
            val operatorId = rs.getInt("operator_id")
            val operatorUsername = rs.getString("operator_username")
            val operatorRole = rs.getString("operator_role").let { Role.valueOf(it) }
            val balance = rs.getBigDecimal("balance")
            val beforeBalance = rs.getBigDecimal("before_balance")
            val remarks = rs.getString("remarks")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }
            ArtificialOrder(id = id, clientId = clientId, memberId = memberId, operatorId = operatorId, operatorRole = operatorRole,
                    balance = balance, beforeBalance = beforeBalance, remarks = remarks, createdTime = createdTime, orderId = orderId,
                    status = status, username = username, operatorUsername = operatorUsername)
        }

    override fun query(query: ArtificialOrderQuery): List<ArtificialOrder> {
        return query()
                .where("client_id", query.clientId)
                .where("operator_role", query.operatorRole)
                .where("member_id", query.memberId)
                .where("operator_id", query.waiterId)
                .limit(query.current, query.size)
                .execute(mapper)
    }

    override fun total(query: ArtificialOrderQuery): Int {
        return query("count(*)")
                .where("client_id", query.clientId)
                .where("operator_role", query.operatorRole)
                .where("member_id", query.memberId)
                .where("operator_id", query.waiterId)
                .count()

    }

    override fun create(artificialOrder: ArtificialOrderCo): Boolean {
        return insert()
                .set("order_id", artificialOrder.orderId)
                .set("client_id", artificialOrder.clientId)
                .set("member_id", artificialOrder.memberId)
                .set("username", artificialOrder.username)
                .set("operator_id", artificialOrder.operatorId)
                .set("operator_username", artificialOrder.operatorUsername)
                .set("operator_role", artificialOrder.operatorRole)
                .set("balance", artificialOrder.balance)
                .set("before_balance", artificialOrder.beforeBalance)
                .set("remarks", artificialOrder.remarks)
                .executeOnlyOne()
    }

    override fun mReport(clientId: Int?, memberId: Int?, startDate: LocalDate): List<ArtificialReportVo> {

        return query("client_id, member_id, sum(balance) as total_amount, count(id) as count")
                .where("client_id", clientId)
                .where("member_id", memberId)
                .asWhere("created_time >= ?", startDate)
                .asWhere("created_time < ?", startDate.plusDays(1))
                .group("client_id, member_id")
                .execute { rs ->

                    val clientId = rs.getInt("client_id")
                    val memberId = rs.getInt("member_id")
                    val totalAmount = rs.getBigDecimal("total_amount")
                    val count = rs.getInt("count")

                    ArtificialReportVo(clientId = clientId, memberId = memberId, totalAmount = totalAmount, count = count)
                }
    }

    override fun cReport(startDate: LocalDate): List<ArtificialCReportVo> {
        return query("client_id, sum(balance) as total_amount, count(id) as count")
                .asWhere("created_time >= ?", startDate)
                .asWhere("created_time < ?", startDate.plusDays(1))
                .group("client_id")
                .execute { rs ->

                    val clientId = rs.getInt("client_id")
                    val totalAmount = rs.getBigDecimal("total_amount")
                    val count = rs.getInt("count")

                    ArtificialCReportVo(clientId = clientId, totalAmount = totalAmount, count = count)
                }
    }
}
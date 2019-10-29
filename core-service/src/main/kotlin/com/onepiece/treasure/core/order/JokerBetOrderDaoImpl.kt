package com.onepiece.treasure.core.order

import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp


@Repository
class JokerBetOrderDaoImpl: BasicDaoImpl<JokerBetOrder>("joker_bet_order"),JokerBetOrderDao {

    override val mapper: (rs: ResultSet) -> JokerBetOrder
        get() = { rs ->
            val oCode = rs.getString("o_code")
            val username = rs.getString("username")
            val gameCode = rs.getString("game_code")
            val description = rs.getString("description")
            val type = rs.getString("type")
            val amount = rs.getBigDecimal("amount")
            val result = rs.getBigDecimal("result")
            val time = rs.getTimestamp("time").toLocalDateTime()
            val appId = rs.getString("app_id")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            JokerBetOrder(oCode = oCode, username = username, gameCode = gameCode, description = description, type = type,
                    amount = amount, result = result, time = time, appId = appId, createdTime = createdTime)
        }

    override fun creates(orders: List<JokerBetOrder>) {
        val sql = insert()
                .set("o_code", "")
                .set("username", "")
                .set("game_code", "")
                .set("description", "")
                .set("type", "")
                .set("amount", "")
                .set("result", "")
                .set("time", "")
                .set("app_id", "")
                .build()
        jdbcTemplate.batchUpdate(sql, object: BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, index: Int) {
                val order = orders[index]
                var x = 0
                ps.setString(++x, order.oCode)
                ps.setString(++x, order.username)
                ps.setString(++x, order.gameCode)
                ps.setString(++x, order.description)
                ps.setString(++x, order.type)
                ps.setBigDecimal(++x, order.amount)
                ps.setBigDecimal(++x, order.result)
                ps.setTimestamp(++x, Timestamp.valueOf(order.time))
                ps.setString(++x, order.appId)
            }

            override fun getBatchSize(): Int {
                return orders.size
            }
        })
    }

    override fun query(query: JokerBetOrderValue.Query): List<JokerBetOrder> {
        return query().asWhere("time > ?", query.startTime)
                .asWhere("time <= ?", query.endTime)
                .where("username", query.username)
                .limit(0, 500)
                .execute(mapper)
    }
}
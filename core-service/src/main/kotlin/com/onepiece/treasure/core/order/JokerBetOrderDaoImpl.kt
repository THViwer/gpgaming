package com.onepiece.treasure.core.order

import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDate


@Repository
class JokerBetOrderDaoImpl: BasicDaoImpl<JokerBetOrder>("joker_bet_order"),JokerBetOrderDao {

    override val mapper: (rs: ResultSet) -> JokerBetOrder
        get() = { rs ->
            val oCode = rs.getString("o_code")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val username = rs.getString("username")
            val gameCode = rs.getString("game_code")
            val description = rs.getString("description")
            val type = rs.getString("type")
            val amount = rs.getBigDecimal("amount")
            val result = rs.getBigDecimal("result")
            val time = rs.getTimestamp("time").toLocalDateTime()
            val appId = rs.getString("app_id")
            val currencyCode = rs.getString("currency_code")
            val details = rs.getString("details")
            val freeAmount = rs.getBigDecimal("free_amount")
            val roundId = rs.getString("round_id")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            JokerBetOrder(oCode = oCode, username = username, gameCode = gameCode, description = description, type = type,
                    amount = amount, result = result, time = time, appId = appId, createdTime = createdTime, clientId = clientId,
                    memberId = memberId, currencyCode = currencyCode, details = details, freeAmount = freeAmount, roundId = roundId)
        }

    override fun creates(orders: List<JokerBetOrder>) {
        val sql = insert()
                .set("o_code", "")
                .set("client_id", "")
                .set("member_id", "")
                .set("username", "")
                .set("game_code", "")
                .set("description", "")
                .set("type", "")
                .set("amount", "")
                .set("result", "")
                .set("time", "")
                .set("app_id", "")
                .set("currency_code", "")
                .set("details", "")
                .set("free_amount", "")
                .set("round_id", "")
                .build()
        jdbcTemplate.batchUpdate(sql, object: BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, index: Int) {
                val order = orders[index]
                var x = 0
                ps.setString(++x, order.oCode)
                ps.setInt(++x, order.clientId)
                ps.setInt(++x, order.memberId)
                ps.setString(++x, order.username)
                ps.setString(++x, order.gameCode)
                ps.setString(++x, order.description)
                ps.setString(++x, order.type)
                ps.setBigDecimal(++x, order.amount)
                ps.setBigDecimal(++x, order.result)
                ps.setTimestamp(++x, Timestamp.valueOf(order.time))
                ps.setString(++x, order.appId)
                ps.setString(++x, order.currencyCode)
                ps.setString(++x, order.details)
                ps.setBigDecimal(++x, order.freeAmount)
                ps.setString(++x, order.roundId)
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

    override fun report(startDate: LocalDate, endDate: LocalDate): List<JokerBetOrderValue.JokerReport> {

        val sql = """
            select 
                client_id, member_id, sum(amount) as amount, sum(result) as result from joker_bet_order 
            where 
                time > ? and time <= ? 
            group by client_id, member_id
        """.trimIndent()

        return jdbcTemplate.query(sql, arrayOf(startDate, endDate)) { rs, _ ->

            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val amount = rs.getBigDecimal("amount")
            val result = rs.getBigDecimal("result")

            JokerBetOrderValue.JokerReport(clientId = clientId, memberId = memberId, amount = amount, result = result)
        }

    }

}
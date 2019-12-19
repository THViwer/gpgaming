package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.BetOrder
import com.onepiece.gpgaming.beans.value.database.BetOrderReport
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.dao.BetOrderDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDate

@Repository
class BetOrderDaoImpl : BasicDaoImpl<BetOrder>("bet_order"), BetOrderDao {

    override val mapper: (rs: ResultSet) -> BetOrder
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val orderId = rs.getString("order_id")
            val betAmount = rs.getBigDecimal("bet_amount")
            val winAmount = rs.getBigDecimal("win_amount")
            val mark = rs.getBoolean("mark")
            val originData = rs.getString("origin_data")
            val betTime = rs.getTimestamp("bet_time").toLocalDateTime()
            val settleTime = rs.getTimestamp("settle_time").toLocalDateTime()
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            BetOrder(id = id, clientId = clientId, memberId = memberId, platform = platform, orderId = orderId, betAmount = betAmount,
                    winAmount = winAmount, mark = mark, originData = originData, betTime = betTime, settleTime = settleTime, createdTime = createdTime)
        }

    override fun batch(orders: List<BetOrderValue.BetOrderCo>) {
        if (orders.isEmpty()) return

        orders.groupBy { this.getRuleKey(it.clientId, it.memberId, it.platform) }.map {
            val table = getRuleTable(it.key)
            this.batch(table = table, orders = it.value)
        }
    }

    private fun batch(table: String, orders: List<BetOrderValue.BetOrderCo>) {

        val sql = insert(table)
                .asSet("client_id")
                .asSet("member_id")
                .asSet("platform")
                .asSet("order_id")
                .asSet("bet_amount")
                .asSet("win_amount")
                .asSet("mark")
                .asSet("origin_data")
                .asSet("bet_time")
                .asSet("settle_time")
                .build()

        jdbcTemplate.batchUpdate(sql, object: BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, index: Int) {
                val order = orders[index]
                var x = 0
                ps.setInt(++x, order.clientId)
                ps.setInt(++x, order.memberId)
                ps.setString(++x, order.platform.name)
                ps.setString(++x, order.orderId)
                ps.setBigDecimal(++x, order.betAmount)
                ps.setBigDecimal(++x, if (order.winAmount.toDouble() > 0) order.winAmount else BigDecimal.ZERO)
                ps.setBoolean(++x, false)
                ps.setString(++x, order.originData)
                ps.setTimestamp(++x, Timestamp.valueOf(order.betTime))
                ps.setTimestamp(++x, Timestamp.valueOf(order.settleTime))
            }

            override fun getBatchSize(): Int {
                return orders.size
            }
        })
    }

    override fun getBets(clientId: Int, memberId: Int, platform: Platform): List<BetOrder> {
        val table = this.getRuleTable(clientId, memberId, platform)

        return query(defaultTable = table)
                .where("client_id", clientId)
                .where("member_id", memberId)
                .where("platform", platform)
                .sort("id desc")
                //TODO 暂时不分页
                .limit(0, 500)
                .execute(mapper)
    }

    override fun getNotMarkBets(table: String, startId: Int): List<BetOrderValue.BetMarkVo> {
        return query(returnColumns = "id, client_id, member_id, platform, bet_amount, win_amount", defaultTable = table)
                .asWhere("id > ?", startId)
                .where("mark", false)
                .limit(0, 5000)
                .execute {  rs ->
                    val id = rs.getInt("id")
                    val clientId = rs.getInt("client_id")
                    val memberId = rs.getInt("member_id")
                    val platform = rs.getString("platform").let { Platform.valueOf(it) }
                    val betAmount = rs.getBigDecimal("bet_amount")
                    val winAmount = rs.getBigDecimal("win_amount")

                    BetOrderValue.BetMarkVo(id = id, clientId = clientId, memberId = memberId, platform = platform, betAmount = betAmount, winAmount = winAmount)
                }

    }

    override fun markBet(table: String, startId: Int, endId: Int): Boolean {

        val count = update(defaultTable = table)
                .set("mark", true)
                .asWhere("id > ?", startId)
                .asWhere("id <= ?", endId)
                .execute()
        return count > 0
    }

    override fun getLastNotMarkId(table: String): Int {
        return query(returnColumns = "id", defaultTable = table)
                .where("mark", false)
                .limit(0, 1)
                .executeMaybeOne { rs ->
                    rs.getInt("id")
                }?: 0
    }

    override fun report(startDate: LocalDate, endDate: LocalDate): List<BetOrderReport> {
        return (0 until 8).map { index ->
            query(returnColumns = "client_id, platform, sum(bet_amount) as totalBet, sum(win_amount) as totalWin" ,defaultTable = "bet_order_$index")
                    .asWhere("settle_time > ?", startDate)
                    .group("client_id, platform")
                    .execute { rs ->

                        val clientId = rs.getInt("client_id")
                        val platform = rs.getString("platform").let { Platform.valueOf(it) }
                        val totalBet = rs.getBigDecimal("totalBet")
                        val totalWin = rs.getBigDecimal("totalWin")
                        BetOrderReport(clientId = clientId, platform = platform, totalBet = totalBet, totalWin = totalWin)
                    }
        }.reduce { acc, list ->  acc.plus(list)}
                .groupBy { "${it.clientId}:${it.platform}" }
                .map {
                    val totalBet = it.value.sumByDouble { it.totalBet.toDouble() }.toBigDecimal().setScale(2, 2)
                    val totalWin = it.value.sumByDouble { it.totalWin.toDouble() }.toBigDecimal().setScale(2, 2)
                    it.value.first().copy(totalBet = totalBet, totalWin = totalWin)
                }

    }
}
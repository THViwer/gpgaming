package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.BetOrder
import com.onepiece.gpgaming.beans.value.database.BetOrderReport
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.beans.value.database.BetReportValue
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
            val validAmount = rs.getBigDecimal("valid_amount")
            val payout = rs.getBigDecimal("payout")
            val mark = rs.getBoolean("mark")
            val originData = rs.getString("origin_data")
            val betTime = rs.getTimestamp("bet_time").toLocalDateTime()
            val settleTime = rs.getTimestamp("settle_time").toLocalDateTime()
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }

            BetOrder(id = id, clientId = clientId, memberId = memberId, platform = platform, orderId = orderId, betAmount = betAmount,
                    payout = payout, mark = mark, originData = originData, betTime = betTime, settleTime = settleTime,
                    createdTime = createdTime, status = status, validAmount = validAmount)
        }

    override fun batch(orders: List<BetOrderValue.BetOrderCo>) {
        if (orders.isEmpty()) return

        orders.groupBy { this.getRuleKey(it.clientId, it.memberId) }.map {
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
                .asSet("valid_amount")
                .asSet("payout")
                .asSet("mark")
                .asSet("origin_data")
                .asSet("bet_time")
                .asSet("settle_time")
                .build()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, index: Int) {
                val order = orders[index]
                var x = 0
                ps.setInt(++x, order.clientId)
                ps.setInt(++x, order.memberId)
                ps.setString(++x, order.platform.name)
                ps.setString(++x, order.orderId)
                ps.setBigDecimal(++x, order.betAmount)
                ps.setBigDecimal(++x, order.validAmount)
                ps.setBigDecimal(++x, if (order.payout.toDouble() > 0) order.payout else BigDecimal.ZERO)
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

    override fun getBets(query: BetOrderValue.BetOrderQuery): List<BetOrder> {
        val table = this.getRuleTable(query.clientId, query.memberId)

        return query(defaultTable = table)
                .where("client_id", query.clientId)
                .where("member_id", query.memberId)
                .where("platform", query.platform)
                .asWhere("bet_time >= ?", query.betStartTime)
                .asWhere("bet_time <= ?", query.betEndTime)
                .sort("id desc")
                //TODO 暂时不分页
                .limit(0, 500)
                .execute(mapper)
    }

    override fun getBets(clientId: Int, memberId: Int, platform: Platform): List<BetOrder> {
        val table = this.getRuleTable(clientId, memberId)

        return query(defaultTable = table)
                .where("client_id", clientId)
                .where("member_id", memberId)
                .where("platform", platform)
                .sort("id desc")
                //TODO 暂时不分页
                .limit(0, 500)
                .execute(mapper)
    }

    override fun last500(clientId: Int, memberId: Int, startDate: LocalDate, endDate: LocalDate): List<BetOrder> {
        val table = this.getRuleTable(clientId, memberId)
        return query(defaultTable = table)
                .asWhere("settle_time > ?", startDate)
                .asWhere("settle_time < ?", endDate)
                .where("client_id", clientId)
                .where("member_id", memberId)
                .sort("bet_time desc")
                .limit(0, 2000)
                .execute(mapper)
    }

    override fun getNotMarkBets(table: String, startId: Int): List<BetOrderValue.BetMarkVo> {
        return query(returnColumns = "id, client_id, member_id, platform, bet_amount, payout", defaultTable = table)
                .asWhere("id > ?", startId)
                .where("mark", false)
                .limit(0, 5000)
                .execute { rs ->
                    val id = rs.getInt("id")
                    val clientId = rs.getInt("client_id")
                    val memberId = rs.getInt("member_id")
                    val platform = rs.getString("platform").let { Platform.valueOf(it) }
                    val betAmount = rs.getBigDecimal("bet_amount")
                    val payout = rs.getBigDecimal("payout")

                    BetOrderValue.BetMarkVo(id = id, clientId = clientId, memberId = memberId, platform = platform, betAmount = betAmount, payout = payout)
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
                } ?: 0
    }

    override fun report(memberId: Int?, startDate: LocalDate, endDate: LocalDate): List<BetOrderReport> {
        return (0 until 8).map { index ->
            query(returnColumns = "client_id, platform, sum(bet_amount) as totalBet, sum(payout) as payout", defaultTable = "bet_order_$index")
                    .asWhere("settle_time > ?", startDate)
                    .asWhere("settle_time < ?", endDate)
                    .where("member_id", memberId)
                    .group("client_id, platform")
                    .execute { rs ->

                        val clientId = rs.getInt("client_id")
                        val platform = rs.getString("platform").let { Platform.valueOf(it) }
                        val totalBet = rs.getBigDecimal("totalBet")
                        val payout = rs.getBigDecimal("payout")
                        BetOrderReport(clientId = clientId, platform = platform, totalBet = totalBet, payout = payout)
                    }
        }.reduce { acc, list -> acc.plus(list) }
                .groupBy { "${it.clientId}:${it.platform}" }
                .map {
                    val totalBet = it.value.sumByDouble { it.totalBet.toDouble() }.toBigDecimal().setScale(2, 2)
                    val payout = it.value.sumByDouble { it.payout.toDouble() }.toBigDecimal().setScale(2, 2)
                    it.value.first().copy(totalBet = totalBet, payout = payout)
                }
    }

    override fun mreport(clientId: Int?, memberId: Int?, startDate: LocalDate): List<BetReportValue.MBetReport> {
        return (0 until 8).map { x ->
            query(returnColumns = "client_id, member_id, platform, sum(bet_amount) as bet, sum(valid_amount) as valid_amount, sum(payout) as payout", defaultTable = "bet_order_$x")
                    .asWhere("settle_time >= ?", startDate)
                    .asWhere("settle_time < ?", startDate.plusDays(1))
                    .where("client_id", clientId)
                    .where("member_id", memberId)
                    .group("client_id, member_id, platform")
                    .execute { rs ->
                        val xClientId = rs.getInt("client_id")
                        val xMemberId = rs.getInt("member_id")
                        val platform = rs.getString("platform").let { Platform.valueOf(it) }
                        val totalBet = rs.getBigDecimal("bet")
                        val payout = rs.getBigDecimal("payout")
                        val validAmount = rs.getBigDecimal("valid_amount")

                        BetReportValue.MBetReport(clientId = xClientId, memberId = xMemberId, platform = platform, totalBet = totalBet, payout = payout, validBet = validAmount)
                    }
        }.reduce { a, b -> a.plus(b) }
    }


    override fun creport(startDate: LocalDate): List<BetReportValue.CBetReport> {
        return (0 until 8).map { x ->
            query(returnColumns = "client_id, sum(bet_amount) as bet, sum(payout) as payout", defaultTable = "bet_order_$x")
                    .asWhere("settle_time >= ?", startDate)
                    .asWhere("settle_time < ?", startDate.plusDays(1))
                    .group("client_id")
                    .execute { rs ->
                        val clientId = rs.getInt("client_id")
                        val totalBet = rs.getBigDecimal("bet")
                        val payout = rs.getBigDecimal("payout")

                        BetReportValue.CBetReport(clientId = clientId, totalBet = totalBet, payout = payout)
                    }
        }.reduce { a, b -> a.plus(b) }.groupBy { it.clientId }.values.map {
            val clientId = it.first().clientId
            val totalBet = it.sumByDouble { it.totalBet.toDouble() }.toBigDecimal()
            val payout = it.sumByDouble { it.payout.toDouble() }.toBigDecimal()

            BetReportValue.CBetReport(clientId = clientId, totalBet = totalBet, payout = payout)
        }
    }

    override fun getTotalBet(clientId: Int, memberId: Int, startDate: LocalDate): BigDecimal {
        return (0..7).map { index ->
            val sql = "select COALESCE(sum(bet_amount), 0) from  bet_order_$index where  client_id  = $clientId and  member_id = $memberId and created_time  > '$startDate'"
            jdbcTemplate.queryForObject(sql, BigDecimal::class.java) ?: BigDecimal.ZERO
        }.sumByDouble { it.toDouble() }
                .toBigDecimal()
                .setScale(2, 2)
    }

    override fun delOldBet(startDate: LocalDate) {
        (0..7).forEach { x ->
            val sql = "delete from bet_order_$x where created_time  < '$startDate'"
            jdbcTemplate.execute(sql)
        }
    }
}
package com.onepiece.treasure.core.order

import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class Cta666BetOrderDaoImpl : BasicDaoImpl<Cta666BetOrder>("cta666_bet_order"), Cta666BetOrderDao {

    override val mapper: (rs: ResultSet) -> Cta666BetOrder
        get() = { rs ->
            val id = rs.getLong("id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val lobbyId = rs.getInt("lobby)_id")
            val tableId = rs.getInt("table_id")
            val shoeId = rs.getLong("shoe_id")
            val playId = rs.getLong("play_id")
            val gameType = rs.getInt("game_type")
            val gameId = rs.getInt("game_id")
            val platformMemberId = rs.getLong("platform_member_id")
            val betTime = rs.getTimestamp("bet_time")?.toLocalDateTime()
            val calTime = rs.getTimestamp("cal_time")?.toLocalDateTime()
            val winOrLoss = rs.getBigDecimal("win_or_loss")
            val winOrLossz = rs.getBigDecimal("win_or_lossz")
            val betPoints = rs.getBigDecimal("bet_points")
            val betPointsz = rs.getBigDecimal("bet_pointsz")
            val availableBet = rs.getBigDecimal("available_bet")
            val username = rs.getString("username")
            val result = rs.getString("result")
            val betDetail = rs.getString("bet_detail")
            val betDetailz = rs.getString("bet_detailz")
            val ip = rs.getString("ip")
            val ext = rs.getString("ext")
            val isRevocation = rs.getInt("is_revocation")
            val balanceBefore = rs.getBigDecimal("balance_before")
            val parentBetId = rs.getLong("parent_bet_id")
            val currencyId = rs.getInt("currency_id")
            val deviceType = rs.getInt("device_type")
            val pluginId = rs.getInt("plugin_id")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            Cta666BetOrder(id = id, clientId = clientId, memberId = memberId, lobbyId = lobbyId, tableId = tableId, shoeId = shoeId,
                    playId = playId, gameType = gameType, gameId = gameId, platformMemberId = platformMemberId, betTime = betTime,
                    calTime = calTime, winOrLoss = winOrLoss, winOrLossz = winOrLossz, betPoints = betPoints, betPointsz = betPointsz,
                    ip = ip, ext = ext, isRevocation = isRevocation, balanceBefore = balanceBefore, parentBetId = parentBetId,
                    currencyId = currencyId, deviceType = deviceType, pluginId = pluginId, createdTime = createdTime, availableBet = availableBet,
                    userName = username, result = result, betDetail = betDetail, betDetailz = betDetailz)
        }

    override fun create(orders: List<Cta666BetOrder>) {
        val sql = insert()
                .set("id", "")
                .set("client_id", "")
                .set("member_id", "")
                .set("lobby_id", "")
                .set("table_id", "")
                .set("shoe_id", "")
                .set("play_id", "")
                .set("game_type", "")
                .set("game_id", "")
                .set("platform_member_id", "")
                .set("bet_time", "")
                .set("cal_time", "")
                .set("win_or_loss", "")
                .set("win_or_lossz", "")
                .set("bet_points", "")
                .set("bet_pointsz", "")
                .set("available_bet", "")
                .set("username", "")
                .set("result", "")
                .set("bet_detail", "")
                .set("bet_detailz", "")
                .set("ip", "")
                .set("ext", "")
                .set("is_revocation", "")
                .set("balance_before", "")
                .set("parent_bet_id", "")
                .set("currency_id", "")
                .set("device_type", "")
                .set("plugin_id", "")
                .build()

        jdbcTemplate.batchUpdate(sql, object: BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, index: Int) {
                val order = orders[index]
                var x = 0
                ps.setLong(++x, order.id)
                ps.setInt(++x, order.clientId)
                ps.setInt(++x, order.memberId)
                ps.setIntOrNull(++x, order.lobbyId)
                ps.setIntOrNull(++x, order.tableId)
                ps.setLongOrNull(++x, order.shoeId)
                ps.setLong(++x, order.playId)
                ps.setInt(++x, order.gameType)
                ps.setInt(++x, order.gameId)
                ps.setLongOrNull(++x, order.platformMemberId)
                ps.setTimestampOrNull(++x, order.betTime)
                ps.setTimestampOrNull(++x, order.calTime)
                ps.setBigDecimal(++x, order.winOrLoss)
                ps.setBigDecimal(++x, order.winOrLossz)
                ps.setBigDecimal(++x, order.betPoints)
                ps.setBigDecimal(++x, order.betPointsz)
                ps.setBigDecimal(++x, order.availableBet)
                ps.setString(++x, order.userName)
                ps.setString(++x, order.result)
                ps.setString(++x, order.betDetail)
                ps.setString(++x, order.betDetailz)
                ps.setString(++x, order.ip)
                ps.setString(++x, order.ext)
                ps.setInt(++x, order.isRevocation)
                ps.setBigDecimal(++x, order.balanceBefore)
                ps.setLongOrNull(++x, order.parentBetId)
                ps.setInt(++x, order.currencyId)
                ps.setInt(++x, order.deviceType)
                ps.setInt(++x, order.pluginId)
            }

            override fun getBatchSize(): Int {
                return orders.size
            }
        })

    }

    override fun query(query: BetOrderValue.Query): List<Cta666BetOrder> {
        return query()
                //TODO 查询条件
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .execute(mapper)

    }

    override fun report(startDate: LocalDate, endDate: LocalDate): List<BetOrderValue.Report> {
        val sql = """
            select 
                client_id, member_id, sum(bet_points) as bet, sum(win_or_loss) as win from cta666_bet_order 
            where 
                cal_time >= ? and cal_time < ? 
            group by client_id, member_id
        """.trimIndent()

        return jdbcTemplate.query(sql, arrayOf(startDate, endDate)) { rs, _ ->

            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val bet = rs.getBigDecimal("bet")
            val win = rs.getBigDecimal("win")

            BetOrderValue.Report(clientId = clientId, memberId = memberId, bet = bet, win = win)
        }

    }
}
package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.ClientPlatformDailyReport
import com.onepiece.treasure.beans.value.database.ClientReportQuery
import com.onepiece.treasure.beans.value.database.ClientReportVo
import com.onepiece.treasure.core.dao.ClientPlatformDailyReportDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.Date
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class ClientPlatformDailyReportDaoImpl : BasicDaoImpl<ClientPlatformDailyReport>("client_platform_daily_report"), ClientPlatformDailyReportDao {

    override val mapper: (rs: ResultSet) -> ClientPlatformDailyReport
        get() = { rs ->
            val id = rs.getInt("id")
            val day = rs.getDate("day").toLocalDate()
            val clientId = rs.getInt("client_id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val bet = rs.getBigDecimal("bet")
            val win = rs.getBigDecimal("win")
            val transferIn = rs.getBigDecimal("transfer_in")
            val transferOut = rs.getBigDecimal("transfer_out")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            ClientPlatformDailyReport(id = id, day = day, clientId = clientId, platform = platform, bet = bet, win = win,
                    transferIn = transferIn, transferOut = transferOut, createdTime = createdTime)
        }

    override fun create(reports: List<ClientPlatformDailyReport>) {
        batchInsert(reports)
                .set("day")
                .set("client_id")
                .set("platform")
                .set("bet")
                .set("win")
                .set("transfer_in")
                .set("transfer_out")
                .execute { ps, entity ->
                    var x = 0
                    ps.setDate(++x, Date.valueOf(entity.day))
                    ps.setInt(++x, entity.clientId)
                    ps.setString(++x, entity.platform.name)
                    ps.setBigDecimal(++x, entity.bet)
                    ps.setBigDecimal(++x, entity.win)
                    ps.setBigDecimal(++x, entity.transferIn)
                    ps.setBigDecimal(++x, entity.transferOut)
                }

    }

    override fun query(query: ClientReportQuery): List<ClientPlatformDailyReport> {
        return query()
                .where("client_id", query.clientId)
                .asWhere("day > ?", query.endDate)
                .asWhere("day <= ?", query.endDate)
                .execute(mapper)

    }

    override fun report(startDate: LocalDate, endDate: LocalDate): List<ClientReportVo> {

        return query("client_id, sum(bet) as bet, sum(win) as win, sum(transfer_in) as transfer_in, sum(transfer_out) as transfer_out")
                .asWhere("day >= ?", startDate)
                .asWhere("day < ?", endDate)
                .group("client_id")
                .execute { rs ->
                    val clientId = rs.getInt("client_id")
                    val bet = rs.getBigDecimal("bet")
                    val win = rs.getBigDecimal("win")
                    val transferIn = rs.getBigDecimal("transfer_in")
                    val transferOut = rs.getBigDecimal("transfer_out")

                    ClientReportVo(clientId = clientId, bet = bet, win = win, transferIn = transferIn, transferOut = transferOut)
                }
    }
}
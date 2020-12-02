package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.ClientPlatformDailyReport
import com.onepiece.gpgaming.beans.value.database.ClientPlatformDailyReportVo
import com.onepiece.gpgaming.beans.value.database.ClientReportQuery
import com.onepiece.gpgaming.core.dao.ClientPlatformDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
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
            val payout = rs.getBigDecimal("payout")
            val transferIn = rs.getBigDecimal("transfer_in")
            val transferOut = rs.getBigDecimal("transfer_out")
            val activeCount = rs.getInt("active_count")
            val promotionAmount = rs.getBigDecimal("promotion_amount")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }


            ClientPlatformDailyReport(day = "$day", clientId = clientId, platform = platform, activeCount = activeCount,
                    transferIn = transferIn, transferOut = transferOut, createdTime = createdTime, bet = bet, payout = payout,
                    promotionAmount = promotionAmount, status = status)
        }

    override fun create(reports: List<ClientPlatformDailyReport>) {
        batchInsert(reports)
                .set("day")
                .set("client_id")
                .set("platform")
                .set("transfer_in")
                .set("transfer_out")
                .set("bet")
                .set("payout")
                .set("active_count")
                .execute { ps, entity ->
                    var x = 0
                    ps.setDate(++x, Date.valueOf(entity.day))
                    ps.setInt(++x, entity.clientId)
                    ps.setString(++x, entity.platform.name)
                    ps.setBigDecimal(++x, entity.transferIn)
                    ps.setBigDecimal(++x, entity.transferOut)
                    ps.setBigDecimal(++x, entity.bet)
                    ps.setBigDecimal(++x, entity.payout)
                    ps.setInt(++x, entity.activeCount)
                }

    }

    override fun query(query: ClientReportQuery): List<ClientPlatformDailyReport> {
        return query()
                .where("client_id", query.clientId)
                .asWhere("day >= ?", query.startDate)
                .asWhere("day < ?", query.endDate)
                .sort("day desc")
                .execute(mapper)

    }

    override fun report(startDate: LocalDate, endDate: LocalDate): List<ClientPlatformDailyReportVo> {

        return query("client_id, sum(transfer_in) as transfer_in, sum(transfer_out) as transfer_out")
                .asWhere("day >= ?", startDate)
                .asWhere("day < ?", endDate)
                .group("client_id")
                .execute { rs ->
                    val clientId = rs.getInt("client_id")
                    val transferIn = rs.getBigDecimal("transfer_in")
                    val transferOut = rs.getBigDecimal("transfer_out")
                    val platform = rs.getString("platform").let { Platform.valueOf(it) }

                    ClientPlatformDailyReportVo(clientId = clientId, transferIn = transferIn, transferOut = transferOut, platform = platform)
                }
    }
}
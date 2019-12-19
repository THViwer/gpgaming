package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.MemberPlatformDailyReport
import com.onepiece.gpgaming.beans.value.database.ClientPlatformDailyReportVo
import com.onepiece.gpgaming.beans.value.database.MemberPlatformDailyReportVo
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.core.dao.MemberPlatformDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.stereotype.Repository
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class MemberPlatformDailyReportDaoImpl : BasicDaoImpl<MemberPlatformDailyReport>("member_platform_daily_report"), MemberPlatformDailyReportDao {

    override val mapper: (rs: ResultSet) -> MemberPlatformDailyReport
        get() = { rs ->
            val id = rs.getInt("id")
            val day = rs.getDate("day").toLocalDate()
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val transferIn = rs.getBigDecimal("transfer_in")
            val transferOut = rs.getBigDecimal("transfer_out")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            MemberPlatformDailyReport(id = id, day = day, clientId = clientId, platform = platform,
                    createdTime = createdTime, memberId = memberId, transferIn = transferIn, transferOut = transferOut)
        }

    override fun create(reports: List<MemberPlatformDailyReport>) {
        val sql = insert()
                .set("day", "")
                .set("client_id", "")
                .set("member_Id", "")
                .set("platform", "")
                .set("transfer_in", "")
                .set("transfer_out", "")
                .build()
        jdbcTemplate.batchUpdate(sql, object: BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, index: Int) {
                val report = reports[index]
                var x = 0
                ps.setDate(++x, Date.valueOf(report.day))
                ps.setInt(++x, report.clientId)
                ps.setInt(++x, report.memberId)
                ps.setString(++x, report.platform.name)
                ps.setBigDecimal(++x, report.transferIn)
                ps.setBigDecimal(++x, report.transferOut)
            }

            override fun getBatchSize(): Int {
                return reports.size
            }
        })
    }

    override fun query(query: MemberReportQuery): List<MemberPlatformDailyReport> {
        return query()
                .where("member_id",query.memberId)
                .asWhere("day > ?", query.startDate)
                .asWhere("day <= ?", query.endDate)
                .execute(mapper)
    }

    override fun report(startDate: LocalDate, endDate: LocalDate): List<MemberPlatformDailyReportVo> {
        return query("client_id, member_id, sum(transfer_in) as transfer_in, sum(transfer_out) as transfer_out")
                .asWhere("day >= ?", startDate)
                .asWhere("day < ?", endDate)
                .group("client_id, member_id")
                .execute { rs ->
                    val clientId = rs.getInt("client_id")
                    val memberId = rs.getInt("member_id")
                    val transferIn = rs.getBigDecimal("transfer_in")
                    val transferOut = rs.getBigDecimal("transfer_out")
                    MemberPlatformDailyReportVo(clientId = clientId, memberId = memberId, transferIn = transferIn,
                            transferOut = transferOut)
                }
    }

    override fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientPlatformDailyReportVo> {
        return query("client_id, platform, sum(bet) as bet, sum(win) as win, sum(transfer_in) as transfer_in, sum(transfer_out) as transfer_out")
                .asWhere("day >= ?", startDate)
                .asWhere("day < ?", endDate)
                .group("client_id, platform")
                .execute { rs ->
                    val clientId = rs.getInt("client_id")
                    val platform = rs.getString("platform").let { Platform.valueOf(it) }
                    val transferIn = rs.getBigDecimal("transfer_in")
                    val transferOut = rs.getBigDecimal("transfer_out")
                    ClientPlatformDailyReportVo(clientId = clientId, platform = platform, transferIn = transferIn,
                            transferOut = transferOut)
                }
    }
}
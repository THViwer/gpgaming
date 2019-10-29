package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.MemberReport
import com.onepiece.treasure.beans.value.database.MemberReportQuery
import com.onepiece.treasure.core.dao.MemberReportDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.stereotype.Repository
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet

@Repository
class MemberReportDaoImpl : BasicDaoImpl<MemberReport>("member_report"), MemberReportDao {

    override val mapper: (rs: ResultSet) -> MemberReport
        get() = { rs ->
            val id = rs.getInt("id")
            val day = rs.getDate("day").toLocalDate()
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val bet = rs.getBigDecimal("bet")
            val money = rs.getBigDecimal("money")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            MemberReport(id = id, day = day, clientId = clientId, platform = platform, bet = bet, money = money,
                    createdTime = createdTime, memberId = memberId)
        }

    override fun creates(reports: List<MemberReport>) {
        val sql = insert()
                .set("day", "")
                .set("client_id", "")
                .set("member_Id", "")
                .set("platform", "")
                .set("bet", "")
                .set("money", "")
                .build()
        jdbcTemplate.batchUpdate(sql, object: BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, index: Int) {
                val report = reports[index]
                var x = 0
                ps.setDate(++x, Date.valueOf(report.day))
                ps.setInt(++x, report.clientId)
                ps.setInt(++x, report.memberId)
                ps.setString(++x, report.platform.name)
                ps.setBigDecimal(++x, report.bet)
                ps.setBigDecimal(++x, report.money)
            }

            override fun getBatchSize(): Int {
                return reports.size
            }
        })
    }

    override fun query(query: MemberReportQuery): List<MemberReport> {
        return query()
                .where("member_id",query.memberId)
                .asWhere("day > ?", query.startDate)
                .asWhere("day <= ?", query.endDate)
                .execute(mapper)
    }
}
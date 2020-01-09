package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.core.dao.MemberDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.Date
import java.sql.ResultSet

@Repository
class MemberDailyReportDaoImpl : BasicDaoImpl<MemberDailyReport>("member_daily_report"), MemberDailyReportDao {

    override val mapper: (rs: ResultSet) -> MemberDailyReport
        get() = { rs ->
            val id = rs.getInt("id")
            val day = rs.getDate("day").toLocalDate()
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val transferIn = rs.getBigDecimal("transfer_in")
            val transferOut = rs.getBigDecimal("transfer_out")
            val depositMoney = rs.getBigDecimal("deposit_money")
            val withdrawMoney = rs.getBigDecimal("withdraw_money")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }

            MemberDailyReport(id = id, day = day, clientId = clientId, memberId = memberId,
                    transferIn = transferIn, transferOut = transferOut, depositMoney = depositMoney, withdrawMoney = withdrawMoney,
                    createdTime = createdTime, status = status)
        }

    override fun create(reports: List<MemberDailyReport>) {

        return batchInsert(reports)
                .set("day")
                .set("client_id")
                .set("member_id")
                .set("transfer_in")
                .set("transfer_out")
                .set("deposit_money")
                .set("withdraw_money")
                .execute { ps, entity ->
                    var index = 0
                    ps.setDate(++index, Date.valueOf(entity.day))
                    ps.setInt(++index, entity.clientId)
                    ps.setInt(++index, entity.memberId)
                    ps.setBigDecimal(++index, entity.transferIn)
                    ps.setBigDecimal(++index, entity.transferOut)
                    ps.setBigDecimal(++index, entity.depositMoney)
                    ps.setBigDecimal(++index, entity.withdrawMoney)
                }

    }

    override fun query(query: MemberReportQuery): List<MemberDailyReport> {

        return query()
                .where("client_id", query.clientId)
                .asWhere("day > ?", query.startDate)
                .asWhere("day <= ?", query.endDate)
                .where("member_id", query.memberId)
                .execute(mapper)
    }
}
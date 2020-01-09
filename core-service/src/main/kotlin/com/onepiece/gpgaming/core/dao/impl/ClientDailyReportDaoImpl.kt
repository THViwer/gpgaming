package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.value.database.ClientReportQuery
import com.onepiece.gpgaming.core.dao.ClientDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.Date
import java.sql.ResultSet

@Repository
class ClientDailyReportDaoImpl : BasicDaoImpl<ClientDailyReport>("client_daily_report"), ClientDailyReportDao {

    override val mapper: (rs: ResultSet) -> ClientDailyReport
        get() = { rs ->
            val id = rs.getInt("id")
            val day = rs.getDate("day").toLocalDate()
            val clientId = rs.getInt("client_id")
            val transferIn = rs.getBigDecimal("transfer_in")
            val transferOut = rs.getBigDecimal("transfer_out")
            val depositMoney = rs.getBigDecimal("deposit_money")
            val depositCount = rs.getInt("deposit_count")
            val withdrawMoney = rs.getBigDecimal("withdraw_money")
            val withdrawCount = rs.getInt("withdraw_count")
            val promotionAmount = rs.getBigDecimal("promotion_amount")
            val newMemberCount = rs.getInt("new_member_count")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }
            ClientDailyReport(id = id, day = day, clientId = clientId, transferIn = transferIn, transferOut = transferOut,
                    depositMoney = depositMoney, depositCount = depositCount, withdrawMoney = withdrawMoney,
                    withdrawCount = withdrawCount, createdTime = createdTime, newMemberCount = newMemberCount,
                    promotionAmount = promotionAmount, status = status)
        }

    override fun create(reports: List<ClientDailyReport>) {
        batchInsert(reports)
                .set("day")
                .set("client_id")
                .set("transfer_in")
                .set("transfer_out")
                .set("deposit_money")
                .set("deposit_count")
                .set("withdraw_money")
                .set("withdraw_count")
                .set("promotion_amount")
                .set("new_member_count")
                .execute { ps, entity ->
                    var x = 0
                    ps.setDate(++x, Date.valueOf(entity.day))
                    ps.setInt(++x, entity.clientId)
                    ps.setBigDecimal(++x, entity.transferIn)
                    ps.setBigDecimal(++x, entity.transferOut)
                    ps.setBigDecimal(++x, entity.depositMoney)
                    ps.setInt(++x, entity.depositCount)
                    ps.setBigDecimal(++x, entity.withdrawMoney)
                    ps.setInt(++x, entity.withdrawCount)
                    ps.setBigDecimal(++x, entity.promotionAmount)
                    ps.setInt(++x, entity.newMemberCount)
                }
    }

    override fun query(query: ClientReportQuery): List<ClientDailyReport> {
        return query()
                .where("client_id", query.clientId)
                .asWhere("day >= ?", query.startDate)
                .asWhere("day < ?", query.endDate)
                .execute(mapper)
    }
}
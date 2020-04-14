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
class fClientDailyReportDaoImpl : BasicDaoImpl<ClientDailyReport>("client_daily_report"), ClientDailyReportDao {

    override val mapper: (rs: ResultSet) -> ClientDailyReport
        get() = { rs ->
            val id = rs.getInt("id")
            val day = rs.getDate("day").toLocalDate()
            val clientId = rs.getInt("client_id")
            val transferIn = rs.getBigDecimal("transfer_in")
            val transferOut = rs.getBigDecimal("transfer_out")
            val depositMoney = rs.getBigDecimal("deposit_money")
            val depositCount = rs.getInt("deposit_count")
            val depositSequence = rs.getInt("deposit_sequence")
            val withdrawMoney = rs.getBigDecimal("withdraw_money")
            val withdrawCount = rs.getInt("withdraw_count")
            val promotionAmount = rs.getBigDecimal("promotion_amount")
            val artificialMoney = rs.getBigDecimal("artificial_money")
            val artificialCount = rs.getInt("artificial_count")
            val newMemberCount = rs.getInt("new_member_count")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }
            val totalBet = rs.getBigDecimal("total_bet")
            val totalMWin = rs.getBigDecimal("total_m_win")
            val thirdPayMoney = rs.getBigDecimal("third_pay_money")
            val thirdPayCount = rs.getInt("third_pay_count")
            val thirdPaySequence = rs.getInt("third_pay_sequence")
            val backwaterMoney = rs.getBigDecimal("backwater_money")

            ClientDailyReport(id = id, day = day, clientId = clientId, transferIn = transferIn, transferOut = transferOut,
                    depositMoney = depositMoney, depositCount = depositCount, withdrawMoney = withdrawMoney,
                    withdrawCount = withdrawCount, createdTime = createdTime, newMemberCount = newMemberCount,
                    promotionAmount = promotionAmount, status = status, artificialMoney = artificialMoney, artificialCount = artificialCount,
                    totalBet = totalBet, totalMWin = totalMWin, thirdPayMoney = thirdPayMoney, thirdPayCount = thirdPayCount,
                    thirdPaySequence = thirdPaySequence, depositSequence = depositSequence, backwaterMoney = backwaterMoney)
        }

    override fun create(reports: List<ClientDailyReport>) {
        batchInsert(reports)
                .set("day")
                .set("client_id")
                .set("transfer_in")
                .set("transfer_out")
                .set("deposit_money")
                .set("deposit_count")
                .set("deposit_sequence")
                .set("withdraw_money")
                .set("withdraw_count")
                .set("promotion_amount")
                .set("artificial_money")
                .set("artificial_count")
                .set("new_member_count")
                .set("total_bet")
                .set("total_m_win")
                .set("third_pay_money")
                .set("third_pay_count")
                .set("third_pay_sequence")
                .set("backwater_money")
                .execute { ps, entity ->
                    var x = 0
                    ps.setDate(++x, Date.valueOf(entity.day))
                    ps.setInt(++x, entity.clientId)
                    ps.setBigDecimal(++x, entity.transferIn)
                    ps.setBigDecimal(++x, entity.transferOut)
                    ps.setBigDecimal(++x, entity.depositMoney)
                    ps.setInt(++x, entity.depositCount)
                    ps.setInt(++x, entity.depositSequence)
                    ps.setBigDecimal(++x, entity.withdrawMoney)
                    ps.setInt(++x, entity.withdrawCount)
                    ps.setBigDecimal(++x, entity.promotionAmount)
                    ps.setBigDecimal(++x, entity.artificialMoney)
                    ps.setInt(++x, entity.artificialCount)
                    ps.setInt(++x, entity.newMemberCount)
                    ps.setBigDecimal(++x, entity.totalBet)
                    ps.setBigDecimal(++x, entity.totalMWin)
                    ps.setBigDecimal(++x, entity.thirdPayMoney)
                    ps.setInt(++x, entity.thirdPayCount)
                    ps.setInt(++x, entity.thirdPaySequence)
                    ps.setBigDecimal(++x, entity.backwaterMoney)
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
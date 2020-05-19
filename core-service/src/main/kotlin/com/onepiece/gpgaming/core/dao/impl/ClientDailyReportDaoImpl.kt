package com.onepiece.gpgaming.core.dao.impl

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
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val transferIn = rs.getBigDecimal("transfer_in")
            val transferOut = rs.getBigDecimal("transfer_out")
            val depositAmount = rs.getBigDecimal("deposit_amount")
            val depositCount = rs.getInt("deposit_count")
            val withdrawAmount = rs.getBigDecimal("withdraw_amount")
            val withdrawCount = rs.getInt("withdraw_count")
            val promotionAmount = rs.getBigDecimal("promotion_amount")
            val artificialAmount = rs.getBigDecimal("artificial_amount")
            val artificialCount = rs.getInt("artificial_count")
            val newMemberCount = rs.getInt("new_member_count")
            val totalBet = rs.getBigDecimal("total_bet")
            val totalMWin = rs.getBigDecimal("total_m_win")
            val thirdPayAmount = rs.getBigDecimal("third_pay_amount")
            val thirdPayCount = rs.getInt("third_pay_count")
            val rebateAmount = rs.getBigDecimal("rebate_amount")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()


            ClientDailyReport(id = id, day = day, bossId = bossId, clientId = clientId, transferIn = transferIn, transferOut = transferOut, depositAmount = depositAmount,
                    depositCount = depositCount, withdrawAmount = withdrawAmount, withdrawCount = withdrawCount, createdTime = createdTime, newMemberCount = newMemberCount,
                    promotionAmount = promotionAmount, artificialAmount = artificialAmount, artificialCount = artificialCount, totalBet = totalBet, totalMWin = totalMWin,
                    thirdPayAmount = thirdPayAmount, thirdPayCount = thirdPayCount, rebateAmount = rebateAmount)
        }

    override fun create(reports: List<ClientDailyReport>) {
        batchInsert(reports)
                .set("day")
                .set("boss_id")
                .set("client_id")
                .set("transfer_in")
                .set("transfer_out")
                .set("deposit_amount")
                .set("deposit_count")
                .set("withdraw_amount")
                .set("withdraw_count")
                .set("promotion_amount")
                .set("artificial_amount")
                .set("artificial_count")
                .set("new_member_count")
                .set("total_bet")
                .set("total_m_win")
                .set("third_pay_amount")
                .set("third_pay_count")
                .set("rebate_amount")
                .execute { ps, entity ->
                    var x = 0
                    ps.setDate(++x, Date.valueOf(entity.day))
                    ps.setInt(++x, entity.bossId)
                    ps.setInt(++x, entity.clientId)
                    ps.setBigDecimal(++x, entity.transferIn)
                    ps.setBigDecimal(++x, entity.transferOut)
                    ps.setBigDecimal(++x, entity.depositAmount)
                    ps.setInt(++x, entity.depositCount)
                    ps.setBigDecimal(++x, entity.withdrawAmount)
                    ps.setInt(++x, entity.withdrawCount)
                    ps.setBigDecimal(++x, entity.promotionAmount)
                    ps.setBigDecimal(++x, entity.artificialAmount)
                    ps.setInt(++x, entity.artificialCount)
                    ps.setInt(++x, entity.newMemberCount)
                    ps.setBigDecimal(++x, entity.totalBet)
                    ps.setBigDecimal(++x, entity.totalMWin)
                    ps.setBigDecimal(++x, entity.thirdPayAmount)
                    ps.setInt(++x, entity.thirdPayCount)
                    ps.setBigDecimal(++x, entity.rebateAmount)
                }
    }

    override fun query(query: ClientReportQuery): List<ClientDailyReport> {
        return query()
                .where("client_id", query.clientId)
                .asWhere("day >= ?", query.startDate)
                .asWhere("day < ?", query.endDate)
                .sort("day desc")
                .execute(mapper)
    }
}
package com.onepiece.gpgaming.core.dao.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
import com.onepiece.gpgaming.core.dao.MemberDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.Date
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class MemberDailyReportDaoImpl(
        private val objectMapper: ObjectMapper
) : BasicDaoImpl<MemberDailyReport>("member_daily_report"), MemberDailyReportDao {

    override val mapper: (rs: ResultSet) -> MemberDailyReport
        get() = { rs ->
            val id = rs.getInt("id")
            val day = rs.getDate("day").toLocalDate()
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val transferIn = rs.getBigDecimal("transfer_in")
            val transferOut = rs.getBigDecimal("transfer_out")
            val depositMoney = rs.getBigDecimal("deposit_money")
            val depositCount = rs.getInt("deposit_count")
            val withdrawMoney = rs.getBigDecimal("withdraw_money")
            val withdrawCount = rs.getInt("withdraw_count")
            val artificialMoney = rs.getBigDecimal("artificial_money")
            val artificialCount = rs.getInt("artificial_count")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }
            val totalBet = rs.getBigDecimal("total_bet")
            val totalMWin = rs.getBigDecimal("total_m_win")
            val settles = rs.getString("settles").let { objectMapper.readValue<List<MemberDailyReport.PlatformSettle>>(it) }
            val thirdPayMoney = rs.getBigDecimal("third_pay_money")
            val thirdPayCount = rs.getInt("third_pay_count")
            val backwater = rs.getBigDecimal("backwater")
            val backwaterMoney = rs.getBigDecimal("backwater_money")
            val backwaterExecution = rs.getBoolean("backwater_execution")
            val promotionMoney = rs.getBigDecimal("promotion_money")

            MemberDailyReport(id = id, day = day, clientId = clientId, memberId = memberId,
                    transferIn = transferIn, transferOut = transferOut, depositMoney = depositMoney, withdrawMoney = withdrawMoney,
                    createdTime = createdTime, status = status, artificialMoney = artificialMoney, artificialCount = artificialCount,
                    depositCount = depositCount, withdrawCount = withdrawCount, settles = settles, totalBet = totalBet, totalMWin = totalMWin,
                    thirdPayMoney = thirdPayMoney, thirdPayCount = thirdPayCount, backwater = backwater, backwaterMoney = backwaterMoney,
                    backwaterExecution = backwaterExecution, promotionMoney = promotionMoney)
        }

    override fun create(reports: List<MemberDailyReport>) {

        return batchInsert(reports)
                .set("day")
                .set("client_id")
                .set("member_id")
                .set("transfer_in")
                .set("transfer_out")
                .set("deposit_money")
                .set("deposit_count")
                .set("withdraw_money")
                .set("withdraw_count")
                .set("artificial_money")
                .set("artificial_count")
                .set("total_bet")
                .set("total_m_win")
                .set("settles")
                .set("third_pay_money")
                .set("third_pay_count")
                .set("backwater")
                .set("backwater_money")
                .set("backwater_execution")
                .set("promotion_money")
                .execute { ps, entity ->
                    var index = 0
                    ps.setDate(++index, Date.valueOf(entity.day))
                    ps.setInt(++index, entity.clientId)
                    ps.setInt(++index, entity.memberId)
                    ps.setBigDecimal(++index, entity.transferIn)
                    ps.setBigDecimal(++index, entity.transferOut)
                    ps.setBigDecimal(++index, entity.depositMoney)
                    ps.setInt(++index, entity.depositCount)
                    ps.setBigDecimal(++index, entity.withdrawMoney)
                    ps.setInt(++index, entity.withdrawCount)
                    ps.setBigDecimal(++index, entity.artificialMoney)
                    ps.setInt(++index, entity.artificialCount)
                    ps.setBigDecimal(++index, entity.totalBet)
                    ps.setBigDecimal(++index, entity.totalMWin)
                    ps.setString(++index, objectMapper.writeValueAsString(entity.settles))
                    ps.setBigDecimal(++index, entity.thirdPayMoney)
                    ps.setInt(++index, entity.thirdPayCount)
                    ps.setBigDecimal(++index, entity.backwater)
                    ps.setBigDecimal(++index, entity.backwaterMoney)
                    ps.setBoolean(++index, entity.backwaterExecution)
                    ps.setBigDecimal(++index, entity.promotionMoney)
                }

    }

    override fun total(query: MemberReportQuery): MemberReportValue.MemberReportTotal {

        val columns = """
            count(*) as count,
            sum(total_m_win) as totalMWin,
            sum(total_bet) as totalBet,
            sum(transfer_in) as transferIn,
            sum(transfer_out) as transferOut,
            sum(deposit_count) as totalDepositCount,
            sum(deposit_money) as totalDepositMoney,
            sum(withdraw_count) as totalWithdrawCount,
            sum(artificial_money) as totalArtificialMoney,
            sum(artificial_count) as totalArtificialCount,
            sum(third_pay_money) as totalThirdPayMoney,
            sum(third_pay_count) as totalThirdPayCount,
            sum(withdraw_money) as totalWithdrawMoney,
            sum(backwater_money) as totalBackwaterMoney,
            sum(promotion_money)as totalPromotionMoney
        """.trimIndent()

        return query(columns)
                .where("client_id", query.clientId)
                .asWhere("day > ?", query.startDate)
                .asWhere("day <= ?", query.endDate)
                .where("member_id", query.memberId)
                .asWhere("backwater_money >= ?", query.minBackwaterMoney)
                .asWhere("promotion_money >= ?", query.minPromotionMoney)
                .executeOnlyOne { rs ->
                    val count = rs.getInt("count")
                    val totalMWin  = rs.getBigDecimal("totalMWin") ?: BigDecimal.ZERO
                    val totalBet  = rs.getBigDecimal("totalBet") ?: BigDecimal.ZERO
                    val transferIn = rs.getBigDecimal("transferIn") ?: BigDecimal.ZERO
                    val transferOut = rs.getBigDecimal("transferOut") ?: BigDecimal.ZERO
                    val totalDepositCount = rs.getInt("totalDepositCount")
                    val totalDepositMoney  = rs.getBigDecimal("totalDepositMoney")?: BigDecimal.ZERO
                    val totalWithdrawCount = rs.getInt("totalWithdrawCount")
                    val  totalArtificialMoney  =  rs.getBigDecimal("totalArtificialMoney")?: BigDecimal.ZERO
                    val totalArtificialCount = rs.getInt("totalArtificialCount")
                    val totalThirdPayMoney = rs.getBigDecimal("totalThirdPayMoney")?: BigDecimal.ZERO
                    val totalThirdPayCount = rs.getInt("totalThirdPayCount")
                    val totalWithdrawMoney = rs.getBigDecimal("totalWithdrawMoney")?: BigDecimal.ZERO
                    val totalBackwaterMoney = rs.getBigDecimal("totalBackwaterMoney")?: BigDecimal.ZERO
                    val totalPromotionMoney = rs.getBigDecimal("totalPromotionMoney")?: BigDecimal.ZERO

                    MemberReportValue.MemberReportTotal(count = count, totalMWin = totalMWin, totalBet = totalBet, transferIn = transferIn,
                            transferOut = transferOut, totalDepositCount = totalDepositCount, totalDepositMoney = totalDepositMoney,
                            totalWithdrawCount = totalWithdrawCount, totalWithdrawMoney = totalWithdrawMoney,
                            totalArtificialCount = totalArtificialCount,  totalArtificialMoney = totalArtificialMoney,
                            totalThirdPayCount = totalThirdPayCount, totalThirdPayMoney = totalThirdPayMoney,
                            totalBackwaterMoney = totalBackwaterMoney, totalPromotionMoney = totalPromotionMoney)
                }
    }

    override fun query(query: MemberReportQuery): List<MemberDailyReport> {

        return query()
                .where("client_id", query.clientId)
                .asWhere("day > ?", query.startDate)
                .asWhere("day <= ?", query.endDate)
                .where("member_id", query.memberId)
                .asWhere("backwater_money >= ?", query.minBackwaterMoney)
                .asWhere("promotion_money >= ?", query.minPromotionMoney)
                .sort("day desc")
                .limit(query.current, query.size)
                .execute(mapper)
    }

    override fun queryBackwater(current: Int, size: Int): List<MemberDailyReport> {
        return query()
                .where("backwater_execution", false)
                .asWhere("backwater_money != 0")
                .limit(current, size)
                .execute(mapper)
    }

    override fun updateBackwater(ids: List<Int>) {
        update()
                .set("backwater_execution", true)
                .asWhere("id in (${ids.joinToString(",")})")
                .execute()
    }

    override fun backwater(startDate: LocalDate): Map<Int, BigDecimal> {
        return query("client_id, sum(backwater_money) as total_backwater_money")
                .asWhere("created_time > ?", startDate)
                .group("client_id")
                .execute { rs ->
                    val clientId = rs.getInt("client_id")
                    val totalBackwaterMoney = rs.getBigDecimal("total_backwater_money")

                    clientId to totalBackwaterMoney
                }.toMap()
    }
}
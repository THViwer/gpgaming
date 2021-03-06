package com.onepiece.gpgaming.core.dao.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.MemberAnalysisSort
import com.onepiece.gpgaming.beans.enums.SaleScope
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.value.database.MarketDailyReportValue
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
import com.onepiece.gpgaming.core.dao.MemberDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.math.BigDecimal
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
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val superiorAgentId = rs.getInt("superior_agent_id")
            val saleId = rs.getInt("sale_id")
            val saleScope = rs.getString("sale_scope").let { SaleScope.valueOf(it) }
            val marketId = rs.getInt("market_id")
            val agentId = rs.getInt("agent_id")
            val memberId = rs.getInt("member_id")
            val username = rs.getString("username")
            val transferIn = rs.getBigDecimal("transfer_in")
            val transferOut = rs.getBigDecimal("transfer_out")
            val depositAmount = rs.getBigDecimal("deposit_amount")
            val depositCount = rs.getInt("deposit_count")
            val withdrawAmount = rs.getBigDecimal("withdraw_amount")
            val withdrawCount = rs.getInt("withdraw_count")
            val artificialAmount = rs.getBigDecimal("artificial_amount")
            val artificialCount = rs.getInt("artificial_count")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }
            val totalBet = rs.getBigDecimal("total_bet")
            val totalMWin = rs.getBigDecimal("total_m_win")
            val settles = rs.getString("settles").let { objectMapper.readValue<List<MemberDailyReport.PlatformSettle>>(it) }
            val thirdPayAmount = rs.getBigDecimal("third_pay_amount")
            val thirdPayCount = rs.getInt("third_pay_count")
            val rebateAmount = rs.getBigDecimal("rebate_amount")
            val rebateExecution = rs.getBoolean("rebate_execution")
            val promotionAmount = rs.getBigDecimal("promotion_amount")

            MemberDailyReport(id = id, day = day, clientId = clientId, memberId = memberId, username = username,
                    transferIn = transferIn, transferOut = transferOut, depositAmount = depositAmount, withdrawAmount = withdrawAmount,
                    createdTime = createdTime, status = status, artificialAmount = artificialAmount, artificialCount = artificialCount,
                    depositCount = depositCount, withdrawCount = withdrawCount, settles = settles, totalBet = totalBet, totalMWin = totalMWin,
                    thirdPayAmount = thirdPayAmount, thirdPayCount = thirdPayCount, rebateAmount = rebateAmount, bossId = bossId,
                    rebateExecution = rebateExecution, promotionAmount = promotionAmount, agentId = agentId, superiorAgentId = superiorAgentId,
                    saleId = saleId, saleScope = saleScope, marketId = marketId)
        }

    override fun create(reports: List<MemberDailyReport>) {

        return batchInsert(reports)
                .set("day")
                .set("boss_id")
                .set("client_id")
                .set("superior_agent_id")
                .set("agent_id")
                .set("sale_id")
                .set("sale_scope")
                .set("market_id")
                .set("member_id")
                .set("username")
                .set("transfer_in")
                .set("transfer_out")
                .set("deposit_amount")
                .set("deposit_count")
                .set("withdraw_amount")
                .set("withdraw_count")
                .set("artificial_amount")
                .set("artificial_count")
                .set("total_bet")
                .set("total_m_win")
                .set("settles")
                .set("third_pay_amount")
                .set("third_pay_count")
                .set("rebate_amount")
                .set("rebate_execution")
                .set("promotion_amount")
                .execute { ps, entity ->
                    var index = 0
                    ps.setString(++index, "${entity.day}")
                    ps.setInt(++index, entity.bossId)
                    ps.setInt(++index, entity.clientId)
                    ps.setInt(++index, entity.superiorAgentId)
                    ps.setInt(++index, entity.agentId)
                    ps.setInt(++index, entity.saleId)
                    ps.setString(++index, entity.saleScope.name)
                    ps.setInt(++index, entity.marketId)
                    ps.setInt(++index, entity.memberId)
                    ps.setString(++index, entity.username)
                    ps.setBigDecimal(++index, entity.transferIn)
                    ps.setBigDecimal(++index, entity.transferOut)
                    ps.setBigDecimal(++index, entity.depositAmount)
                    ps.setInt(++index, entity.depositCount)
                    ps.setBigDecimal(++index, entity.withdrawAmount)
                    ps.setInt(++index, entity.withdrawCount)
                    ps.setBigDecimal(++index, entity.artificialAmount)
                    ps.setInt(++index, entity.artificialCount)
                    ps.setBigDecimal(++index, entity.totalBet)
                    ps.setBigDecimal(++index, entity.totalMWin)
                    ps.setString(++index, objectMapper.writeValueAsString(entity.settles))
                    ps.setBigDecimal(++index, entity.thirdPayAmount)
                    ps.setInt(++index, entity.thirdPayCount)
                    ps.setBigDecimal(++index, entity.rebateAmount)
                    ps.setBoolean(++index, entity.rebateExecution)
                    ps.setBigDecimal(++index, entity.promotionAmount)
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
            sum(deposit_amount) as totalDepositAmount,
            sum(withdraw_count) as totalWithdrawCount,
            sum(artificial_amount) as totalArtificialAmount,
            sum(artificial_count) as totalArtificialCount,
            sum(third_pay_amount) as totalThirdPayAmount,
            sum(third_pay_count) as totalThirdPayCount,
            sum(withdraw_amount) as totalWithdrawAmount,
            sum(rebate_amount) as totalRebateAmount,
            sum(promotion_amount)as totalPromotionAmount
        """.trimIndent()

        return query(columns)
                .where("client_id", query.clientId)
                .asWhere("day >= ?", query.startDate)
                .asWhere("day < ?", query.endDate)
                .where("member_id", query.memberId)
                .asWhere("rebate_amount >= ?", query.minRebateAmount)
                .asWhere("promotion_amount >= ?", query.minPromotionAmount)
                .executeOnlyOne { rs ->
                    val count = rs.getInt("count")
                    val totalMWin = rs.getBigDecimal("totalMWin") ?: BigDecimal.ZERO
                    val totalBet = rs.getBigDecimal("totalBet") ?: BigDecimal.ZERO
                    val transferIn = rs.getBigDecimal("transferIn") ?: BigDecimal.ZERO
                    val transferOut = rs.getBigDecimal("transferOut") ?: BigDecimal.ZERO
                    val totalDepositCount = rs.getInt("totalDepositCount")
                    val totalDepositAmount = rs.getBigDecimal("totalDepositAmount") ?: BigDecimal.ZERO
                    val totalWithdrawCount = rs.getInt("totalWithdrawCount")
                    val totalArtificialAmount = rs.getBigDecimal("totalArtificialAmount") ?: BigDecimal.ZERO
                    val totalArtificialCount = rs.getInt("totalArtificialCount")
                    val totalThirdPayAmount = rs.getBigDecimal("totalThirdPayAmount") ?: BigDecimal.ZERO
                    val totalThirdPayCount = rs.getInt("totalThirdPayCount")
                    val totalWithdrawAmount = rs.getBigDecimal("totalWithdrawAmount") ?: BigDecimal.ZERO
                    val totalRebateAmount = rs.getBigDecimal("totalRebateAmount") ?: BigDecimal.ZERO
                    val totalPromotionAmount = rs.getBigDecimal("totalPromotionAmount") ?: BigDecimal.ZERO

                    MemberReportValue.MemberReportTotal(count = count, totalMWin = totalMWin, totalBet = totalBet, transferIn = transferIn,
                            transferOut = transferOut, totalDepositCount = totalDepositCount, totalDepositAmount = totalDepositAmount,
                            totalWithdrawCount = totalWithdrawCount, totalWithdrawAmount = totalWithdrawAmount,
                            totalArtificialCount = totalArtificialCount, totalArtificialAmount = totalArtificialAmount,
                            totalThirdPayCount = totalThirdPayCount, totalThirdPayAmount = totalThirdPayAmount,
                            totalRebateAmount = totalRebateAmount, totalPromotionAmount = totalPromotionAmount)
                }
    }

    override fun query(query: MemberReportQuery): List<MemberDailyReport> {

        return query()
                .where("client_id", query.clientId)
                .where("agent_id", query.agentId)
                .asWhere("day >= ?", query.startDate)
                .asWhere("day < ?", query.endDate)
                .where("member_id", query.memberId)
                .asWhere("rebate >= ?", query.minRebateAmount)
                .asWhere("promotion_amount >= ?", query.minPromotionAmount)
                .sort("day desc")
                .limit(query.current, query.size)
                .execute(mapper)
    }

    override fun queryRebate(current: Int, size: Int): List<MemberDailyReport> {
        return query()
                .where("rebate_execution", false)
                .asWhere("rebate_amount != 0")
                .limit(current, size)
                .execute(mapper)
    }

    override fun updateRebate(ids: List<Int>) {
        update()
                .set("rebate_execution", true)
                .asWhere("id in (${ids.joinToString(",")})")
                .execute()
    }

    override fun rebate(startDate: LocalDate): Map<Int, BigDecimal> {
        return query("client_id, sum(backwater_money) as total_backwater_money")
                .asWhere("day >= ?", startDate)
                .asWhere("day < ?", startDate.plusDays(1))
                .group("client_id")
                .execute { rs ->
                    val clientId = rs.getInt("client_id")
                    val totalBackwaterMoney = rs.getBigDecimal("total_backwater_money")

                    clientId to totalBackwaterMoney
                }.toMap()
    }

    override fun analysis(query: MemberReportValue.AnalysisQuery): List<MemberReportValue.AnalysisVo> {

        val sortBy = when (query.sort) {
            MemberAnalysisSort.WithdrawMax -> "withdraw_amount"
            MemberAnalysisSort.WithdrawSeqMax -> "withdraw_count"
            MemberAnalysisSort.DepositMax -> "deposit_amount"
            MemberAnalysisSort.DepositSeqMax -> "deposit_count"
            MemberAnalysisSort.WinMax -> "total_m_win"
            MemberAnalysisSort.LossMax -> "total_m_loss"
            MemberAnalysisSort.PromotionMax -> "promotion_amount"
        }

        val memberColumn = if (query.memberId != null) " and member_id = ${query.memberId}" else ""
        val sql = """
            select * from (
            	select 
            		member_id,
                    username,
            		sum(total_bet) total_bet,
            		sum(total_m_win) total_m_win,
            		sum(total_bet-total_m_win) total_m_loss,
            		sum(deposit_amount+third_pay_amount) deposit_amount,
            		sum(deposit_count+third_pay_count) deposit_count,
            		sum(withdraw_amount) withdraw_amount,
            		sum(withdraw_count) withdraw_count,
            		sum(artificial_amount) artificial_amount,
            		sum(artificial_count) artificial_count,
            		sum(rebate_amount) rebate_amount,
            		sum(promotion_amount) promotion_amount
            	from member_daily_report 
            	where day >= '${query.startDate}' and day < '${query.endDate}' and client_id = ${query.clientId} $memberColumn group by member_id, username
            ) as t order by t.${sortBy} desc limit ${query.size};
        """.trimIndent()
        return jdbcTemplate.query(sql) { rs, _ ->
            val memberId = rs.getInt("member_id")
            val username = rs.getString("username")
            val totalBet = rs.getBigDecimal("total_bet")
            val totalMWin = rs.getBigDecimal("total_m_win")
            val totalMLoss = rs.getBigDecimal("total_m_loss")
            val depositAmount = rs.getBigDecimal("deposit_amount")
            val depositCount = rs.getInt("deposit_count")
            val withdrawAmount = rs.getBigDecimal("withdraw_amount")
            val withdrawCount = rs.getInt("withdraw_count")
            val artificialAmount = rs.getBigDecimal("artificial_amount")
            val artificialCount = rs.getInt("artificial_count")
            val rebateAmount = rs.getBigDecimal("rebate_amount")
            val promotionAmount = rs.getBigDecimal("promotion_amount")

            MemberReportValue.AnalysisVo(memberId = memberId, username = username, totalBet = totalBet, totalMWin = totalMWin, totalMLoss = totalMLoss,
                    depositAmount = depositAmount, depositCount = depositCount, withdrawAmount = withdrawAmount, withdrawCount = withdrawCount,
                    artificialAmount = artificialAmount, artificialCount = artificialCount, rebateAmount = rebateAmount,
                    promotionAmount = promotionAmount, clientId = query.clientId)
        }
    }

    override fun collect(query: MemberReportValue.CollectQuery): List<MemberReportValue.MemberMonthReport> {

        val columns = """
            boss_id,
            client_id,
            member_id,
            username,
            agent_id,
            superior_agent_id,
            sum(total_bet) total_bet,
            sum(total_m_win) total_m_win,
            sum(transfer_in) transfer_in,
            sum(transfer_out) transfer_out,
            sum(deposit_count) deposit_count,
            sum(deposit_amount) deposit_amount,
            sum(withdraw_count) withdraw_count,
            sum(withdraw_amount) withdraw_amount,
            sum(third_pay_count) third_pay_count,
            sum(third_pay_amount) third_pay_amount,
            sum(promotion_amount) promotion_amount,
            sum(rebate_amount) rebate_amount,
            sum(artificial_amount) artificial_amount,
            sum(artificial_count) artificial_count
        """.trimIndent()
        return query(columns)
                .asWhere("day >= ?", query.startDate)
                .asWhere("day <= ?", query.endDate)
                .where("boss_id", query.bossId)
                .where("client_id", query.clientId)
                .where("agent_id", query.agentId)
                .where("sale_id", query.saleId)
                .where("username", query.username)
                .group("boss_id, client_id, superior_agent_id, agent_id, member_id, username")
                .execute { rs ->

                    val bossId = rs.getInt("boss_id")
                    val clientId = rs.getInt("client_id")
                    val superiorAgentId = rs.getInt("superior_agent_id")
                    val agentId = rs.getInt("agent_id")
                    val memberId = rs.getInt("member_id")
                    val username = rs.getString("username")
                    val transferIn = rs.getBigDecimal("transfer_in")
                    val transferOut = rs.getBigDecimal("transfer_out")
                    val depositAmount = rs.getBigDecimal("deposit_amount")
                    val depositCount = rs.getInt("deposit_count")
                    val withdrawAmount = rs.getBigDecimal("withdraw_amount")
                    val withdrawCount = rs.getInt("withdraw_count")
                    val artificialAmount = rs.getBigDecimal("artificial_amount")
                    val artificialCount = rs.getInt("artificial_count")
                    val totalBet = rs.getBigDecimal("total_bet")
                    val totalMWin = rs.getBigDecimal("total_m_win")
                    val thirdPayAmount = rs.getBigDecimal("third_pay_amount")
                    val thirdPayCount = rs.getInt("third_pay_count")
                    val rebateAmount = rs.getBigDecimal("rebate_amount")
                    val promotionAmount = rs.getBigDecimal("promotion_amount")


                    MemberReportValue.MemberMonthReport(bossId = bossId, clientId = clientId, agentId = agentId, memberId = memberId, username = username,
                            transferIn = transferIn, transferOut = transferOut, depositAmount = depositAmount, depositCount = depositCount, withdrawAmount = withdrawAmount,
                            withdrawCount = withdrawCount, artificialAmount = artificialAmount, artificialCount = artificialCount, totalBet = totalBet, totalMWin = totalMWin,
                            thirdPayAmount = thirdPayAmount, thirdPayCount = thirdPayCount, rebateAmount = rebateAmount, promotionAmount = promotionAmount,
                            superiorAgentId = superiorAgentId, day = query.startDate)
                }

    }

    override fun saleCollect(query: MemberReportValue.MemberCollectQuery): List<MemberReportValue.SaleReportVo> {

        val saleColumn = query.saleId?.let { " and sale_id = ${query.saleId}" } ?: ""

        val sql = """
            select 
            	boss_id, 
            	client_id, 
            	sale_id,
                sale_scope,
            	sum(deposit_amount + third_pay_amount) total_deposit,
            	sum(withdraw_amount) total_withdraw,
            	sum(promotion_amount) total_promotion,
            	sum(rebate_amount) total_rebate
            from member_daily_report where day = '${query.day} $saleColumn'
            group by boss_id, client_id, sale_id, sale_scope;
        """.trimIndent()

        return jdbcTemplate.query(sql) { rs, _ ->

            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val saleId = rs.getInt("sale_id")
            val saleScope = rs.getString("sale_scope").let { SaleScope.valueOf(it) }
            val totalDeposit = rs.getBigDecimal("total_deposit")
            val totalWithdraw = rs.getBigDecimal("total_withdraw")
            val totalPromotion = rs.getBigDecimal("total_promotion")
            val totalRebate = rs.getBigDecimal("total_rebate")

            MemberReportValue.SaleReportVo(bossId = bossId, clientId = clientId, saleId = saleId, totalDeposit = totalDeposit,
                    totalWithdraw = totalWithdraw, totalPromotion = totalPromotion, totalRebate = totalRebate, saleScope = saleScope)

        }
    }

    override fun markCollect(day: LocalDate): List<MarketDailyReportValue.MarketDailyReportCo> {
        val sql = """
            select 
            	client_id,  market_id, sum(deposit_amount + third_pay_amount) deposit_amount, sum(withdraw_amount) withdraw_amount, sum(total_bet) bet 
            from member_daily_report 
            where day = '$day' and market_id != -1 group by client_id,  market_id;
        """.trimIndent()

        return jdbcTemplate.query(sql) { rs, _ ->
            val clientId = rs.getInt("client_id")
            val marketId = rs.getInt("market_id")
            val depositAmount = rs.getBigDecimal("deposit_amount")
            val withdrawAmount = rs.getBigDecimal("withdraw_amount")
            val bet = rs.getBigDecimal("bet")

            MarketDailyReportValue.MarketDailyReportCo(day = day, clientId = clientId, marketId = marketId, registerCount = 0, viewCount = 0, depositAmount = depositAmount,
                    withdrawAmount = withdrawAmount, bet = bet)
        }

    }
}
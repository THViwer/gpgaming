package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.AgentMonthReport
import com.onepiece.gpgaming.beans.value.database.AgentReportValue
import com.onepiece.gpgaming.core.dao.AgentMonthReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class AgentMonthReportDaoImpl : BasicDaoImpl<AgentMonthReport>("agent_month_report"), AgentMonthReportDao {

    override val mapper: (rs: ResultSet) -> AgentMonthReport
        get() = { rs ->

            val id = rs.getInt("id")
            val day = rs.getDate("day").toLocalDate()
            val superiorAgentId = rs.getInt("superior_agent_id")
//            val superiorUsername = rs.getString("superior_jsername")
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val agentId = rs.getInt("agent_id")
            val username = rs.getString("username")
            val agentCommission = rs.getBigDecimal("agent_commission")
            val agentActiveCount = rs.getInt("agent_active_count")
            val agentCommissionScale = rs.getBigDecimal("agent_commission_scale")
            val memberCommission  = rs.getBigDecimal("member_commission")

            val memberActiveCount = rs.getInt("member_active_count")
            val totalDeposit = rs.getBigDecimal("total_deposit")
            val totalWithdraw = rs.getBigDecimal("total_withdraw")
            val totalBet = rs.getBigDecimal("total_bet")
            val totalMWin = rs.getBigDecimal("total_m_win")
            val memberCommissionScale = rs.getBigDecimal("member_commission_scale")
            val totalRebate  =  rs.getBigDecimal("total_rebate")
            val totalPromotion  = rs.getBigDecimal("total_promotion")
            val commissionExecution = rs.getBoolean("commission_execution")
            val newMemberCount = rs.getInt("new_member_count")
            val agencyMonthFee = rs.getBigDecimal("agency_month_fee")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            AgentMonthReport(id  =  id, day = day, bossId = bossId, clientId = clientId, superiorAgentId = superiorAgentId, agentId = agentId, agentCommission = agentCommission,
                    agentActiveCount = agentActiveCount, agentCommissionScale = agentCommissionScale, memberCommission = memberCommission, memberActiveCount = memberActiveCount,
                    totalDeposit = totalDeposit, totalWithdraw = totalWithdraw, totalBet = totalBet, totalMWin = totalMWin,  memberCommissionScale = memberCommissionScale,
                    createdTime = createdTime, totalRebate = totalRebate, totalPromotion = totalPromotion, commissionExecution = commissionExecution, newMemberCount = newMemberCount,
                    agencyMonthFee = agencyMonthFee, username = username)

        }

    override fun create(data: List<AgentMonthReport>) {
        batchInsert(data)
                .set("day")
                .set("boss_id")
                .set("client_id")
                .set("superior_agent_id")
                .set("agent_id")
                .set("username")

                .set("agent_active_count")
                .set("agent_commission_scale")
                .set("agent_commission")

                .set("member_active_count")
                .set("member_commission")
                .set("member_commission_scale")

                .set("total_deposit")
                .set("total_withdraw")
                .set("total_bet")
                .set("total_m_win")
                .set("commission_execution")
                .set("agency_month_fee")
                .execute { ps, entity ->
                    var x = 0
                    ps.setString(++x, entity.day.toString())
                    ps.setInt(++x, entity.bossId)
                    ps.setInt(++x, entity.clientId)
                    ps.setInt(++x, entity.superiorAgentId)
                    ps.setInt(++x, entity.agentId)
                    ps.setString(++x, entity.username)

                    ps.setInt(++x, entity.agentActiveCount)
                    ps.setBigDecimal(++x, entity.agentCommissionScale)
                    ps.setBigDecimal(++x, entity.agentCommission)

                    ps.setInt(++x,  entity.memberActiveCount)
                    ps.setBigDecimal(++x, entity.memberCommission)
                    ps.setBigDecimal(++x, entity.memberCommissionScale)


                    ps.setBigDecimal(++x,  entity.totalDeposit)
                    ps.setBigDecimal(++x, entity.totalWithdraw)
                    ps.setBigDecimal(++x, entity.totalBet)
                    ps.setBigDecimal(++x, entity.totalMWin)
                    ps.setBoolean(++x, entity.commissionExecution)
                    ps.setBigDecimal(++x, entity.agencyMonthFee)
                }
    }

    override fun query(query: AgentReportValue.AgentMonthQuery): List<AgentMonthReport> {
        return query()
                .where("boss_id", query.bossId)
                .where("client_id", query.clientId)
                .where("superior_agent_id", query.superiorAgentId)
                .where("agent_id", query.agentId)
                .asWhere("day >= ?", query.startDate)
                .asWhere("day < ?", query.endDate)
                .execute(mapper)
    }

    override fun commissions(): List<AgentMonthReport> {
        return  query()
                .where("commission_execution", false)
                .execute(mapper)
    }

    override fun executionCommission(ids: List<Int>) {
        val v = ids.joinToString(separator = ",")
        update()
                .set("commission_execution", true)
                .asWhere(" id in ($v)")
                .execute()
    }

}
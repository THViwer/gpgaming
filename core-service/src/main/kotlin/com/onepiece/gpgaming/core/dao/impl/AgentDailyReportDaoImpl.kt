package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.AgentDailyReport
import com.onepiece.gpgaming.beans.value.database.AgentReportValue
import com.onepiece.gpgaming.core.dao.AgentDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class AgentDailyReportDaoImpl : BasicDaoImpl<AgentDailyReport>("agent_daily_report"), AgentDailyReportDao {

    override val mapper: (rs: ResultSet) -> AgentDailyReport
        get() = { rs ->

            val id = rs.getInt("id")
            val bossId =  rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val agentId = rs.getInt("agent_id")
            val usernaem = rs.getString("username")
            val superiorAgentId = rs.getInt("superior_agent_id")
            val totalDeposit = rs.getBigDecimal("total_deposit")
            val totalWithdraw = rs.getBigDecimal("total_withdraw")
            val totalBet = rs.getBigDecimal("total_bet")
            val totalMWin = rs.getBigDecimal("total_m_win")
            val totalRebate = rs.getBigDecimal("total_rebate")
            val totalPromotion = rs.getBigDecimal("total_promotion")
            val newMemberCount = rs.getInt("new_member_count")
            val day = rs.getDate("day").toLocalDate()
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            AgentDailyReport(id = id, bossId = bossId, clientId = clientId, agentId = agentId, totalDeposit = totalDeposit, totalWithdraw = totalWithdraw,
                    totalBet = totalBet, totalMWin = totalMWin, newMemberCount = newMemberCount, day = day, createdTime = createdTime,
                    superiorAgentId = superiorAgentId, totalRebate = totalRebate, totalPromotion = totalPromotion, username = usernaem)
        }

    override fun create(data: List<AgentDailyReport>) {
        return  batchInsert(data)
                .set("boss_id")
                .set("client_id")
                .set("agent_id")
                .set("username")
                .set("superior_agent_id")
                .set("total_deposit")
                .set("total_withdraw")
                .set("total_bet")
                .set("total_m_win")
                .set("total_rebate")
                .set("total_promotion")
                .set("new_member_count")
                .set("day")
                .execute { ps, entity ->
                    var x = 0
                    ps.setInt(++x, entity.bossId)
                    ps.setInt(++x, entity.clientId)
                    ps.setInt(++x, entity.agentId)
                    ps.setString(++x, entity.username)
                    ps.setInt(++x,  entity.superiorAgentId)
                    ps.setBigDecimal(++x, entity.totalDeposit)
                    ps.setBigDecimal(++x, entity.totalWithdraw)
                    ps.setBigDecimal(++x, entity.totalBet)
                    ps.setBigDecimal(++x, entity.totalMWin)
                    ps.setBigDecimal(++x, entity.totalRebate)
                    ps.setBigDecimal(++x, entity.totalPromotion)
                    ps.setInt(++x, entity.newMemberCount)
                    ps.setString(++x, entity.day.toString())
                }
    }

    override fun query(query: AgentReportValue.AgentDailyQuery): List<AgentDailyReport> {
        return query()
                .where("boss_id", query.bossId)
                .where("agent_id", query.agentId)
                .execute(mapper)
    }
}
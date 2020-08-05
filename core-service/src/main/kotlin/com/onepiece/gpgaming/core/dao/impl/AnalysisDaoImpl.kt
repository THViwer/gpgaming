package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.SaleScope
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.AgentDailyReport
import com.onepiece.gpgaming.beans.model.AgentMonthReport
import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.value.database.AgentValue
import com.onepiece.gpgaming.beans.value.database.AnalysisValue
import com.onepiece.gpgaming.core.dao.AnalysisDao
import com.onepiece.gpgaming.utils.Query
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class AnalysisDaoImpl(
        private val jdbcTemplate: JdbcTemplate
) : AnalysisDao {

    fun getQuery(defaultTable: String, returnColumns: String?): Query {
        return Query(jdbcTemplate, defaultTable, returnColumns)
    }

    override fun memberReport(memberId: Int?, startDate: LocalDate, endDate: LocalDate): List<MemberDailyReport> {

        val tmq  = memberId?.let { " and m.id = $it" }?: ""
        val sql =  """
            select
                   m.boss_id,
                   m.client_id,
                   x.agent_id superior_agent_id,
                   m.agent_id,
                   m.sale_id,
                   m.sale_scope,
                   m.id,
                   m.level_id,
                   m.username,
                   IFNULL(d.total_deposit,0) total_deposit,
                   IFNULL(d.deposit_count,0) deposit_count,
                   IFNULL(p.third_pay_amount,0) third_pay_amount,
                   IFNULL(p.third_pay_count,0) third_pay_count,
                   IFNULL(a.artificial_amount, 0) artificial_amount,
                   IFNULL(a.artificial_count, 0) artificial_count,
                   IFNULL(w.total_withdraw,0) total_withdraw,
                   IFNULL(w.withdraw_count,0) withdraw_count,
                   IFNULL(t1.transfer_out, 0) transfer_out,
                   IFNULL(t1.promotion_amount, 0) promotion_amount,
                   IFNULL(t1.requirement_bet, 0) requirement_bet,
                   IFNULL(st.slot_requirement_bet, 0) slot_requirement_bet,
                   IFNULL(lt.live_requirement_bet, 0) live_requirement_bet,
                   IFNULL(spt.sport_requirement_bet, 0) sport_requirement_bet,
                   IFNULL(ft.fish_requirement_bet, 0) fish_requirement_bet,
                   IFNULL(t2.transfer_in, 0) transfer_in
            from member m
                left join (
                    select member_id, sum(money) total_deposit, count(*) deposit_count from deposit d
                    where d.created_time > '$startDate' and d.created_time < '${endDate}' and d.state = 'Successful' group by member_id
                ) d on m.id = d.member_id
                left join (
                    select member_id, sum(amount) third_pay_amount, count(*) third_pay_count from pay_order p
                    where p.created_time > '$startDate' and p.created_time < '$endDate' and p.state = 'Successful' group by member_id
                ) p on m.id = p.member_id
                left join (
                    select member_id, sum(money) artificial_amount, count(*) artificial_count from artificial_order a
                    where a.created_time > '$startDate' and a.created_time < '$endDate' group by member_id
                ) a on m.id = a.member_id
                left join (
                    select member_id, sum(money) total_withdraw, count(*) withdraw_count from withdraw w
                    where w.created_time > '$startDate' and w.created_time < '$endDate'  and w.state = 'Successful' group  by member_id
                ) w on m.id = w.member_id
                left join (
                    select t.member_id, sum(money) transfer_out, sum(promotion_amount) promotion_amount, sum(requirement_bet) requirement_bet from transfer_order t
                    where t.created_time > '$startDate' and t.created_time < '$endDate' and state = 'Successful' and t.`from` = 'Center' group by member_id
                ) t1 on m.id = t1.member_id
            
                left join (
                    select t.member_id,  sum(requirement_bet) slot_requirement_bet from transfer_order t
                    where t.created_time > '$startDate' and t.created_time < '$endDate' and state = 'Successful' and t.`from` = 'Center'
                      and t.`to` in ('Joker', 'Kiss918', 'Pussy888', 'Mega', 'Pragmatic', 'SpadeGaming', 'TTG', 'MicroGaming', 'PlaytechSlot', 'PNG', 'GamePlay', 'SimplePlay', 'AsiaGamingSlot')
                    group by member_id
                ) st on m.id = st.member_id
                left join (
                    select t.member_id,  sum(requirement_bet) live_requirement_bet from transfer_order t
                    where t.created_time > '$startDate' and t.created_time < '$endDate' and state = 'Successful' and t.`from` = 'Center'
                      and t.`to` in ('CT', 'DreamGaming', 'Evolution', 'GoldDeluxe', 'SexyGaming', 'Fgg', 'AllBet', 'SaGaming', 'AsiaGamingLive', 'MicroGamingLive', 'PlaytechLive', 'EBet')
                    group by member_id
                ) lt on m.id = lt.member_id
                left join (
                    select t.member_id,  sum(requirement_bet) sport_requirement_bet from transfer_order t
                    where t.created_time > '$startDate' and t.created_time < '$endDate' and state = 'Successful' and t.`from` = 'Center'
                      and t.`to` in ('Lbc', 'Bcs', 'CMD')
                    group by member_id
                ) spt on m.id = spt.member_id
                left join (
                    select t.member_id,  sum(requirement_bet) fish_requirement_bet from transfer_order t
                    where t.created_time > '$startDate' and t.created_time < '$endDate' and state = 'Successful' and t.`from` = 'Center'
                      and t.`to` in ('GGFishing')
                    group by member_id
                ) ft on m.id = ft.member_id
            
                left join (
                    select t.member_id, sum(money) transfer_in from transfer_order t
                    where t.created_time > '$startDate' and t.created_time < '$endDate' and state = 'Successful' and t.`to` = 'Center' group by member_id
                ) t2 on m.id = t2.member_id
                
                left join member x on x.id = m.agent_id
            where m.role  = 'Member' ${tmq};
        """.trimIndent()

        return jdbcTemplate.query(sql, RowMapper {  rs, _ ->

            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val superiorAgentId = rs.getInt("superior_agent_id")
            val agentId =  rs.getInt("agent_id")
            val saleId = rs.getInt("sale_id")
            val saleScope = rs.getString("sale_scope").let { SaleScope.valueOf(it) }
            val tMemberId = rs.getInt("id")
            val levelId = rs.getInt("level_id")
            val username  = rs.getString("username")
            val totalDeposit = rs.getBigDecimal("total_deposit")
            val depositCount = rs.getInt("deposit_count")
            val thirdPayAmount = rs.getBigDecimal("third_pay_amount")
            val thirdPayCount = rs.getInt("third_pay_count")
            val artificialAmount = rs.getBigDecimal("artificial_amount")
            val artificialCount  = rs.getInt("artificial_count")
            val totalWithdraw = rs.getBigDecimal("total_withdraw")
            val withdrawCount = rs.getInt("withdraw_count")
            val transferOut  = rs.getBigDecimal("transfer_out")
            val promotionAmount = rs.getBigDecimal("promotion_amount")
            val transferIn =  rs.getBigDecimal("transfer_in")

            val slotRequirementBet = rs.getBigDecimal("slot_requirement_bet")
            val liveRequirementBet = rs.getBigDecimal("live_requirement_bet")
            val sportRequirementBet = rs.getBigDecimal("sport_requirement_bet")
            val fishRequirementBet = rs.getBigDecimal("fish_requirement_bet")


            val report = MemberDailyReport(id = -1, bossId = bossId, clientId = clientId, agentId = agentId, memberId = tMemberId, username = username, depositAmount = totalDeposit,
                    depositCount = depositCount, thirdPayAmount = thirdPayAmount, thirdPayCount = thirdPayCount, artificialAmount = artificialAmount, artificialCount = artificialCount,
                    withdrawAmount = totalWithdraw, withdrawCount = withdrawCount, transferOut = transferOut, promotionAmount = promotionAmount, transferIn = transferIn,
                    rebateAmount = BigDecimal.ZERO, rebateExecution = true, day = startDate, settles = emptyList(), totalBet = BigDecimal.ZERO, totalMWin = BigDecimal.ZERO,
                    status = Status.Normal, createdTime = LocalDateTime.now(), superiorAgentId = superiorAgentId, saleId = saleId, saleScope = saleScope)

            report.expand(levelId = levelId, slotRequirementBet = slotRequirementBet, liveRequirementBet = liveRequirementBet, sportRequirementBet = sportRequirementBet,
                    fishRequirementBet = fishRequirementBet)
        })
    }

    override fun memberActiveCollect(startDate: LocalDate, endDate: LocalDate): List<AnalysisValue.ActiveCollect> {
        val sql = """
            select
                   agent_id,
                   count(*) active_count
            from member_daily_report
            where day >= '$startDate' and day < '$endDate' and (deposit_amount >= 0 or third_pay_amount >= 0)
            group by agent_id;
        """.trimIndent()

        return jdbcTemplate.query(sql) { rs, _ ->
            val agentId = rs.getInt("agent_id")
            val activeCount = rs.getInt("active_count")

            AnalysisValue.ActiveCollect(agentId = agentId, activeCount = activeCount)
        }
    }

    override fun agentReport(startDate: LocalDate, endDate: LocalDate): List<AgentDailyReport> {

        val sql = """
            select 
            	m.boss_id,
                m.client_id,
            	m.id agent_id,
                m.username username,
                m.agent_id superior_agent_id,
                IFNULL(mr.total_bet, 0) total_bet,
                IFNULL(mr.total_m_win, 0) total_m_win,
                IFNULL(mr.total_deposit, 0) total_deposit,
                IFNULL(mr.total_withdraw, 0) total_withdraw,
                IFNULL(mr.total_rebate, 0) total_rebate,
                IFNULL(mr.total_promotion, 0) total_promotion,
                IFNULL(mc.new_member_count, 0) new_member_count
            from member m 
            left join  (
            		select 
            			boss_id, 
            			agent_id, 
            			sum(total_bet) total_bet, 
            			sum(total_m_win) total_m_win,
            			sum(deposit_amount + third_pay_amount) total_deposit,
                        sum(withdraw_amount) total_withdraw,
            			sum(rebate_amount) total_rebate,
            			sum(promotion_amount) total_promotion
            		from member_daily_report where day = '${startDate}' group by boss_id, agent_id
            ) mr on m.id = mr.agent_id 
            left join (
            	select 
                    agent_id, 
                    count(*) new_member_count 
                from member mc 
                    where mc.created_time > '${startDate}' and mc.created_time < '${endDate}' group by agent_id 
            ) mc on m.id = mc.agent_id 
            where m.role = 'Agent';
        """.trimIndent()

        return jdbcTemplate.query(sql, RowMapper { rs, _ ->
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val agentId = rs.getInt("agent_id")
            val usernaem = rs.getString("username")
            val superiorAgentId =  rs.getInt("superior_agent_id")
            val totalBet = rs.getBigDecimal("total_bet")
            val totalMWin = rs.getBigDecimal("total_m_win")
            val totalDeposit  = rs.getBigDecimal("total_deposit")
            val totalWithdraw = rs.getBigDecimal("total_withdraw")
            val totalRebate  = rs.getBigDecimal("total_rebate")
            val totalPromotion  = rs.getBigDecimal("total_promotion")
            val newMemberCount = rs.getInt("new_member_count")

            AgentDailyReport(id = -1, bossId = bossId, clientId = clientId, agentId = agentId, totalBet = totalBet, totalMWin = totalMWin,
                    totalDeposit = totalDeposit, totalWithdraw = totalWithdraw, totalRebate = totalRebate,
                    totalPromotion = totalPromotion, newMemberCount = newMemberCount, day = startDate,
                    superiorAgentId = superiorAgentId, createdTime = LocalDateTime.now(), username = usernaem)
        })
    }

    override fun agentActiveCollect(startDate: LocalDate, endDate: LocalDate): List<AnalysisValue.ActiveCollect> {
        val sql = """
            select
                   superior_agent_id,
                   count(*) active_count
            from member_daily_report
            where day >= '$startDate' and day < '$endDate'
            group by superior_agent_id;
        """.trimIndent()

        return jdbcTemplate.query(sql) { rs, _ ->
            val agentId = rs.getInt("superior_agent_id")
            val activeCount = rs.getInt("active_count")

            AnalysisValue.ActiveCollect(agentId = agentId, activeCount = activeCount)
        }
    }

    override fun agentReportCollect(startDate: LocalDate, endDate: LocalDate): List<AgentDailyReport> {
        val sql = """
            select
                   boss_id,
                   client_id,
                   superior_agent_id,
                   username,
                   sum(total_deposit) total_deposit,
                   sum(total_withdraw) total_withdraw,
                   sum(total_bet) total_bet,
                   sum(total_m_win) total_m_win,
                   sum(total_rebate) total_rebate,
                   sum(total_promotion) total_promotion,
                   count(new_member_count) new_member_count
            from agent_daily_report where day >= '$startDate' and  day  < '$endDate'
            group by boss_id, client_id, superior_agent_id;
        """.trimIndent()

        return  jdbcTemplate.query(sql) { rs, _ ->
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val username = rs.getString("username")
            val superiorAgentId = rs.getInt("superior_agent_id")
            val totalDeposit = rs.getBigDecimal("total_deposit")
            val totalWithdraw = rs.getBigDecimal("total_withdraw")
            val totalBet = rs.getBigDecimal("total_bet")
            val totalMWin = rs.getBigDecimal("total_m_win")
            val totalRebate = rs.getBigDecimal("total_rebate")
            val totalPromotion = rs.getBigDecimal("total_promotion")
            val newMemberCount = rs.getInt("new_member_count")
            AgentDailyReport(id = -1, bossId = bossId, superiorAgentId = superiorAgentId, agentId = -1, totalDeposit = totalDeposit, totalWithdraw = totalWithdraw,
                    totalBet = totalBet, totalMWin = totalMWin, totalRebate = totalRebate, totalPromotion = totalPromotion, newMemberCount = newMemberCount,
                    createdTime = LocalDateTime.now(), day = startDate, clientId = clientId, username = username)
        }

    }

    override fun agentMonthReport(agentId: Int?, startDate: LocalDate, endDate: LocalDate): List<AgentMonthReport> {

        val append = if (agentId != null) " and agent_id = $agentId" else ""
        val sql  = """
            select
                   boss_id,
                   client_id,
                   superior_agent_id,
                   agent_id,
                   sum(total_deposit) total_deposit,
                   sum(total_withdraw) total_withdraw,
                   sum(total_bet) total_bet,
                   sum(total_m_win) total_m_win,
                   sum(total_rebate) total_rebate,
                   sum(total_promotion) total_promotion,
                   sum(new_member_count) new_member_count
            from agent_daily_report
                where day > '$startDate' and day < '$endDate' $append
                group by boss_id, client_id, superior_agent_id, agent_id
        """.trimIndent()

        return jdbcTemplate.query(sql) { rs, _ ->

            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val mAgentId = rs.getInt("agent_id")
            val superiorAgentId = rs.getInt("superior_agent_id")
            val totalDeposit = rs.getBigDecimal("total_deposit")
            val totalWithdraw = rs.getBigDecimal("total_withdraw")
            val totalBet = rs.getBigDecimal("total_bet")
            val totalMWin  = rs.getBigDecimal("total_m_win")
            val totalRebate = rs.getBigDecimal("total_rebate")
            val totalPromotion = rs.getBigDecimal("total_promotion")
            val newMemberCount =  rs.getInt("new_member_count")

            AgentMonthReport(id  = -1, bossId = bossId, superiorAgentId = superiorAgentId, agentId = mAgentId, totalDeposit = totalDeposit,
                    totalWithdraw = totalWithdraw, totalBet = totalBet, totalMWin = totalMWin, totalPromotion = totalPromotion, totalRebate = totalRebate,
                    day = startDate, agentCommissionScale = BigDecimal.ZERO, agentActiveCount = 0, agentCommission = BigDecimal.ZERO,
                    memberCommissionScale = BigDecimal.ZERO, memberActiveCount = 0, memberCommission = BigDecimal.ZERO,
                    createdTime = LocalDateTime.now(), clientId = clientId, commissionExecution = false, newMemberCount = newMemberCount,
                    agencyMonthFee = BigDecimal.ZERO, username = "None")
        }
    }


    override fun clientReport(startDate: LocalDate, endDate: LocalDate): List<ClientDailyReport> {
        val sql = """
            select
                   r.boss_id,
                   r.client_id,
                   sum(r.total_bet) total_bet,
                   sum(r.total_m_win) total_m_win,
                   sum(r.transfer_in) transfer_in,
                   sum(r.transfer_out) transfer_out,
                   sum(r.artificial_amount) artificial_amount,
                   sum(r.artificial_count) artificial_count,
                   sum(r.third_pay_amount) third_pay_amount,
                   sum(r.third_pay_count) third_pay_count,
                   sum(r.deposit_amount) deposit_amount,
                   sum(r.deposit_count) deposit_count,
                   sum(r.withdraw_amount) withdraw_amount,
                   sum(r.withdraw_count) withdraw_count,
                   sum(r.rebate_amount) rebate_amount,
                   sum(r.promotion_amount) promotion_amount,
                   x.new_member_count new_member_count
            from member_daily_report r
                left join (
                    select client_id, count(*) new_member_count from member where `role` = 'Member' and created_time >= '$startDate' and created_time < '$endDate' group by client_id
                ) x on r.client_id = x.client_id
            where  r.day >= '$startDate' and r.day < '$endDate' group  by boss_id, client_id
        """.trimIndent()

        return jdbcTemplate.query(sql) {  rs, _ ->
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val totalBet = rs.getBigDecimal("total_bet")
            val totalMWin = rs.getBigDecimal("total_m_win")
            val transferIn = rs.getBigDecimal("transfer_in")
            val transferOut = rs.getBigDecimal("transfer_out")
            val artificialAmount = rs.getBigDecimal("artificial_amount")
            val artificialCount = rs.getInt("artificial_count")
            val thirdPayAmount = rs.getBigDecimal("third_pay_amount")
            val thirdPayCount = rs.getInt("third_pay_count")
            val depositAmount = rs.getBigDecimal("deposit_amount")
            val depositCount = rs.getInt("deposit_count")
            val withdrawAmount = rs.getBigDecimal("withdraw_amount")
            val withdrawCount = rs.getInt("withdraw_count")
            val rebateAmount = rs.getBigDecimal("rebate_amount")
            val promotionAmount = rs.getBigDecimal("promotion_amount")
            val newMemberCount = rs.getInt("new_member_count")

            ClientDailyReport(id = -1, day = startDate, bossId = bossId, clientId = clientId, totalBet = totalBet, totalMWin = totalMWin,
                    transferIn = transferIn, transferOut = transferOut, artificialAmount = artificialAmount, artificialCount = artificialCount,
                    depositAmount = depositAmount, depositCount = depositCount, withdrawAmount = withdrawAmount, withdrawCount = withdrawCount,
                    rebateAmount = rebateAmount, promotionAmount = promotionAmount, thirdPayAmount = thirdPayAmount, thirdPayCount = thirdPayCount,
                    newMemberCount = newMemberCount, createdTime = LocalDateTime.now(), activeCount = 0)

        }

    }

//    override fun analysis(startDate: LocalDate, endDate: LocalDate, clientId: Int, memberIds: List<Int>?, sort: MemberAnalysisSort, size: Int): List<MemberValue.AnalysisData> {
//
//
//        val idsStr = if (memberIds.isNullOrEmpty()) {
//            ""
//        } else {
//            " and member_id in (${memberIds.joinToString(separator = ",")})"
//        }
//
//        when (sort) {
//
//            MemberAnalysisSort.WithdrawMax -> {
//                """
//                    select * from (
//                        select member_id, sum(money) v from withdraw where created_time > ? and created_time < ? and client_id = ? $idsStr group by member_id
//                    ) t order by v limit $size
//                """.trimIndent()
//                        .let { listOf(it) }
//            }
//            MemberAnalysisSort.WithdrawSeqMax -> {
//                """
//                    select * from (
//                        select member_id, count(id) v from withdraw where created_time > ? and created_time < ? and client_id = ? $idsStr group by member_id
//                    ) t order by v limit $size
//                """.trimIndent()
//                        .let { listOf(it) }
//            }
//            MemberAnalysisSort.DepositMax -> {
//                val a = """
//                    select * from (
//                        select member_id, sum(money) v from deposit where created_time > ? and created_time < ? and client_id = ? $idsStr group by member_id
//                    ) t order by v limit $size
//                """.trimIndent()
//
//
//                val b = """
//                    select * from (
//                        select member_id, count(id) v from pay_order where created_time > ? and created_time < ? and client_id = ? $idsStr group by member_id
//                    ) t order by v limit $size
//                """.trimIndent()
//
//                listOf(a, b)
//            }
//            MemberAnalysisSort.DepositSeqMax -> {
//                val a = """
//                    select * from (
//                        select member_id, sum(amount)  v from pay_order where created_time > ? and created_time < ? and client_id = ? $idsStr group by member_id
//                    ) t order by v limit $size
//                """.trimIndent()
//
//
//                val b = """
//                    select * from (
//                        select member_id, count(id) v from pay_order where created_time > ? and created_time < ? and client_id = ? $idsStr group by member_id
//                    ) t order by v limit $size
//                """.trimIndent()
//
//                listOf(a, b)
//
//            }
//            MemberAnalysisSort.WinMax -> {
//                """
//                    select * from (
//                        select member_id, count(id) v from member_daily_report where day >= ? and day < ? and client_xid = ? $idsStr group by member_id
//                    ) t order by v limit $size
//                """.trimIndent()
//                        .let { listOf(it) }
//
//
//            }
//            MemberAnalysisSort.LossMax -> {
//
//            }
//            MemberAnalysisSort.PromotionMax -> {
//
//            }
//        }
//
//        TODO("Not yet implemented")
//    }

    override fun memberCount(agentId: Int, role: Role): Int {
        val sql = "select count(*) count from `member` where agent_id = $agentId and `role` = '${role.name}'"

        return jdbcTemplate.query(sql) { rs, _ ->
            rs.getInt("count")
        }.first()
    }

    override fun subAgents(bossId: Int, clientId: Int, agentId: Int): List<AgentValue.SubAgentVo> {

        val qParam = if (agentId != -1) " and m.id = $agentId" else ""

        val sql = """
            select m.id, m.agent_id superior_agent_id,m.username, m.phone, m.name, m.agency_month_fee, m.created_time, m.formal, t.count member_count, y.count agent_count from member m
                left join (
                    select agent_id, count(*) count from member x where role = 'Member' group by boss_id, agent_id
                ) t on m.id = t.agent_id
                left join (
                    select agent_id, count(*) count from member x where role = 'Agent' group by boss_id, agent_id
                ) y on m.id = y.agent_id
            where m.boss_id = '$bossId' and client_id = '$clientId' $qParam and `role` = 'Agent' order by m.id desc ;
        """.trimIndent()

        return jdbcTemplate.query(sql) { rs, _ ->

            val id = rs.getInt("id")
            val superiorAgentId = rs.getInt("superior_agent_id")
            val username = rs.getString("username")
            val name = rs.getString("name")
            val phone = rs.getString("phone")
            val formal = rs.getBoolean("formal")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val memberCount = rs.getInt("member_count")
            val agentCount = rs.getInt("agent_count")

            val agencyMonthFee = rs.getBigDecimal("agency_month_fee")

            AgentValue.SubAgentVo(id = id, username = username, phone = phone, formal = formal, memberCount = memberCount,
                    createdTime = createdTime, agencyMonthFee = agencyMonthFee, name = name, superiorAgentId = superiorAgentId,
                    superiorUsername = "-", subAgentCount = agentCount)
        }
    }

    override fun activeCount(startDate: LocalDate, endDate: LocalDate): Map<Int, Int> {

        val sql = """
            select client_id, count(distinct(username)) count  from (
            	select client_id, username from deposit where  state = 'Successful' and created_time > '${startDate}' and created_time < '${endDate}'
            	union all select client_id, username from pay_order where state = 'Successful' and created_time > '${startDate}' and created_time < '${endDate}'
            	union all select client_id, username from withdraw where state = 'Successful' and created_time > '${startDate}' and created_time < '${endDate}'
            ) t group by client_id;
        """.trimIndent()

        return jdbcTemplate.query(sql) { rs, _ ->
            val clientId = rs.getInt("client_id")
            val count = rs.getInt("count")
            clientId to count
        }.toMap()
    }

}

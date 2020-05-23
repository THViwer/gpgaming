package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.enums.MemberAnalysisSort
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.model.AgentDailyReport
import com.onepiece.gpgaming.beans.model.AgentMonthReport
import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.value.database.AgentValue
import com.onepiece.gpgaming.beans.value.database.AnalysisValue
import com.onepiece.gpgaming.beans.value.database.MemberValue
import java.time.LocalDate

interface AnalysisDao  {

    /**
     * 会员日报表
     */
    fun memberReport(startDate: LocalDate, endDate: LocalDate): List<MemberDailyReport>

    /**
     * 会员存活人数汇总
     */
    fun memberActiveCollect(startDate: LocalDate, endDate: LocalDate): List<AnalysisValue.ActiveCollect>

    /**
     * 代理日报表
     */
    fun agentReport(startDate: LocalDate, endDate: LocalDate): List<AgentDailyReport>

    /**
     * 会员存活人数汇总
     */
    fun agentActiveCollect(startDate: LocalDate, endDate: LocalDate): List<AnalysisValue.ActiveCollect>


    /**
     * 代理佣金汇总
     */
    fun agentReportCollect(startDate: LocalDate, endDate: LocalDate):  List<AgentDailyReport>

    /**
     * 代理月报瑶
     */
    fun agentMonthReport(agentId: Int? = null, startDate: LocalDate, endDate: LocalDate): List<AgentMonthReport>

    /**
     * 业主日报表
     */
    fun clientReport(startDate: LocalDate, endDate: LocalDate): List<ClientDailyReport>

    /**
     * 统计总数
     */
    fun memberCount(agentId: Int, role: Role):  Int


    /**
     * 查询下级代理列表
     */
    fun subAgents(bossId: Int, clientId: Int, agentId: Int): List<AgentValue.SubAgentVo>




    fun analysis(startDate: LocalDate, endDate: LocalDate, clientId: Int, memberIds: List<Int>?, sort: MemberAnalysisSort, size: Int): List<MemberValue.AnalysisData>


}
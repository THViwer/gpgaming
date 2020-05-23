package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.AgentDailyReport
import com.onepiece.gpgaming.beans.model.AgentMonthReport
import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.model.ClientPlatformDailyReport
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.model.MemberPlatformDailyReport
import java.time.LocalDate

interface ReportService {

    /**
     * 会员平台报表
     */
    fun startMemberPlatformDailyReport(startDate: LocalDate): List<MemberPlatformDailyReport>

    /**
     * 会员报表
     */
    fun startMemberReport(startDate: LocalDate): List<MemberDailyReport>

    /**
     * 代理日报表
     */
    fun startAgentReport(startDate: LocalDate): List<AgentDailyReport>

    /**
     * 代理月报表
     */
    fun startAgentMonthReport(agentId: Int? = null, today: LocalDate): List<AgentMonthReport>

    /**
     * 厅主平台报表
     */
    fun startClientPlatformReport(startDate: LocalDate): List<ClientPlatformDailyReport>

    /**
     * 厅主报表
     */
    fun startClientReport(startDate: LocalDate):List<ClientDailyReport>

}
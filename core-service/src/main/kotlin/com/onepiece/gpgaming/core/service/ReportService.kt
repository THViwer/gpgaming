package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.AgentDailyReport
import com.onepiece.gpgaming.beans.model.AgentMonthReport
import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.model.ClientPlatformDailyReport
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.model.MemberPlatformDailyReport
import com.onepiece.gpgaming.beans.model.SaleDailyReport
import com.onepiece.gpgaming.beans.model.SaleMonthReport
import com.onepiece.gpgaming.beans.value.database.MarketDailyReportValue
import java.time.LocalDate

interface ReportService {

    /**
     * 会员平台报表
     */
    fun startMemberPlatformDailyReport(startDate: LocalDate): List<MemberPlatformDailyReport>

    /**
     * 会员报表
     */
    fun startMemberReport(memberId: Int? = null, startDate: LocalDate): List<MemberDailyReport>

    /**
     * 代理日报表
     */
    fun startAgentReport(startDate: LocalDate): List<AgentDailyReport>

    /**
     * 代理月报表
     */
    fun startAgentMonthReport(agentId: Int? = null, today: LocalDate): List<AgentMonthReport>

    /**
     * 电销日报表
     */
    fun startSaleReport(startDate: LocalDate): List<SaleDailyReport>

    /**
     * 营销日报表
     */
    fun startMarkReport(startDate: LocalDate): List<MarketDailyReportValue.MarketDailyReportCo>

    /**
     * 电销月报表e
     */
    fun startSaleMonthReport(startDate: LocalDate): List<SaleMonthReport>


    /**
     * 厅主平台报表
     */
    fun startClientPlatformReport(startDate: LocalDate): List<ClientPlatformDailyReport>

    /**
     * 厅主报表
     */
    fun startClientReport(startDate: LocalDate):List<ClientDailyReport>

}
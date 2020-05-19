package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.AgentDailyReport
import com.onepiece.gpgaming.beans.value.database.AgentReportValue

interface AgentDailyReportService  {

    fun create(data: List<AgentDailyReport>)

    fun query(query: AgentReportValue.AgentDailyQuery): List<AgentDailyReport>
}
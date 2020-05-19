package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.AgentDailyReport
import com.onepiece.gpgaming.beans.value.database.AgentReportValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface AgentDailyReportDao: BasicDao<AgentDailyReport> {

    fun create(data: List<AgentDailyReport>)

    fun query(query: AgentReportValue.AgentDailyQuery): List<AgentDailyReport>

}
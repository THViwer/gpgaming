package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.AgentDailyReport
import com.onepiece.gpgaming.beans.value.database.AgentReportValue
import com.onepiece.gpgaming.core.dao.AgentDailyReportDao
import com.onepiece.gpgaming.core.service.AgentDailyReportService
import org.springframework.stereotype.Service

@Service
class AgentDailyReportServiceImpl(
        private val agentDailyReportDao: AgentDailyReportDao
) : AgentDailyReportService {

    override fun create(data: List<AgentDailyReport>) {
        agentDailyReportDao.create(data = data)
    }

    override fun query(query: AgentReportValue.AgentDailyQuery): List<AgentDailyReport> {
        return  agentDailyReportDao.query(query = query)
    }
}
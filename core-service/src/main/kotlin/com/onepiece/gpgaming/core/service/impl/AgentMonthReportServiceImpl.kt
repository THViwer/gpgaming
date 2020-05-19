package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.AgentMonthReport
import com.onepiece.gpgaming.beans.value.database.AgentReportValue
import com.onepiece.gpgaming.core.dao.AgentMonthReportDao
import com.onepiece.gpgaming.core.service.AgentMonthReportService
import org.springframework.stereotype.Service

@Service
class AgentMonthReportServiceImpl(
        private val agentMonthReportDao: AgentMonthReportDao
) : AgentMonthReportService {

    override fun create(data: List<AgentMonthReport>) {
        agentMonthReportDao.create(data = data)
    }

    override fun query(query: AgentReportValue.AgentMonthQuery): List<AgentMonthReport> {
        return agentMonthReportDao.query(query = query)
    }

    override fun commissions(): List<AgentMonthReport> {
        return agentMonthReportDao.commissions()
    }

    override fun executionCommission(ids: List<Int>) {
        agentMonthReportDao.executionCommission(ids = ids)
    }
}
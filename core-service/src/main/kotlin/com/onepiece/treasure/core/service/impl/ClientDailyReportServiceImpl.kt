package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.model.ClientDailyReport
import com.onepiece.treasure.beans.value.database.ClientReportQuery
import com.onepiece.treasure.core.dao.ClientDailyReportDao
import com.onepiece.treasure.core.service.ClientDailyReportService
import org.springframework.stereotype.Service

@Service
class ClientDailyReportServiceImpl(
        private val clientDailyReportDao: ClientDailyReportDao
) : ClientDailyReportService {

    override fun create(reports: List<ClientDailyReport>) {
        return clientDailyReportDao.create(reports)
    }

    override fun query(query: ClientReportQuery): List<ClientDailyReport> {
        return clientDailyReportDao.query(query)
    }
}
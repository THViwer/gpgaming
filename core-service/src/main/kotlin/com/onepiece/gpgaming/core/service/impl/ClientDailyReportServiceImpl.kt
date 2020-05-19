package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.value.database.ClientReportQuery
import com.onepiece.gpgaming.core.dao.ClientDailyReportDao
import com.onepiece.gpgaming.core.service.ClientDailyReportService
import org.springframework.stereotype.Service

@Service
class ClientDailyReportServiceImpl(
        private val clientDailyReportDao: ClientDailyReportDao
) : ClientDailyReportService {

    override fun create(data: List<ClientDailyReport>) {
        return clientDailyReportDao.create(data)
    }

    override fun query(query: ClientReportQuery): List<ClientDailyReport> {
        return clientDailyReportDao.query(query)
    }
}
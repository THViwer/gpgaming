package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.model.ClientPlatformDailyReport
import com.onepiece.treasure.beans.value.database.ClientReportQuery
import com.onepiece.treasure.beans.value.database.ClientReportVo
import com.onepiece.treasure.core.dao.ClientPlatformDailyReportDao
import com.onepiece.treasure.core.service.ClientPlatformDailyReportService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ClientPlatformDailyReportServiceImpl(
        private val clientPlatformDailyReportDao: ClientPlatformDailyReportDao
) : ClientPlatformDailyReportService {

    override fun create(reports: List<ClientPlatformDailyReport>) {
        return clientPlatformDailyReportDao.create(reports)
    }

    override fun query(query: ClientReportQuery): List<ClientPlatformDailyReport> {
        return clientPlatformDailyReportDao.query(query)
    }

    override fun report(startDate: LocalDate, endDate: LocalDate): List<ClientReportVo> {
        return clientPlatformDailyReportDao.report(startDate, endDate)
    }
}
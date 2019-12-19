package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.ClientPlatformDailyReport
import com.onepiece.gpgaming.beans.value.database.ClientPlatformDailyReportVo
import com.onepiece.gpgaming.beans.value.database.ClientReportQuery
import com.onepiece.gpgaming.core.dao.ClientPlatformDailyReportDao
import com.onepiece.gpgaming.core.service.ClientPlatformDailyReportService
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

    override fun report(startDate: LocalDate, endDate: LocalDate): List<ClientPlatformDailyReportVo> {
        return clientPlatformDailyReportDao.report(startDate, endDate)
    }
}
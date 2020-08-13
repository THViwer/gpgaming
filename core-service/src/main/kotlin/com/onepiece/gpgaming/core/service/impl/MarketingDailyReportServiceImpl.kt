package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.MarketingDailyReport
import com.onepiece.gpgaming.beans.value.database.MarketingDailyReportValue
import com.onepiece.gpgaming.core.dao.MarketingDailyReportDao
import com.onepiece.gpgaming.core.service.MarketingDailyReportService
import org.springframework.stereotype.Service

@Service
class MarketingDailyReportServiceImpl (
        private val marketingDailyReportDao: MarketingDailyReportDao
) : MarketingDailyReportService {

    override fun list(query: MarketingDailyReportValue.MarketingDailyReportQuery): List<MarketingDailyReport> {
        return  marketingDailyReportDao.list(query = query)
    }

    override fun batch(data: List<MarketingDailyReportValue.MarketingDailyReportCo>) {
        marketingDailyReportDao.batch(data = data)
    }
}
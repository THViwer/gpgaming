package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.MarketDailyReport
import com.onepiece.gpgaming.beans.value.database.MarketDailyReportValue
import com.onepiece.gpgaming.core.dao.MarketDailyReportDao
import com.onepiece.gpgaming.core.service.MarketDailyReportService
import org.springframework.stereotype.Service

@Service
class MarketDailyReportServiceImpl (
        private val marketingDailyReportDao: MarketDailyReportDao
) : MarketDailyReportService {

    override fun list(query: MarketDailyReportValue.MarketDailyReportQuery): List<MarketDailyReport> {
        return  marketingDailyReportDao.list(query = query)
    }

    override fun batch(data: List<MarketDailyReportValue.MarketDailyReportCo>) {
        marketingDailyReportDao.batch(data = data)
    }
}
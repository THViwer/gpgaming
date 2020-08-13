package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.MarketDailyReport
import com.onepiece.gpgaming.beans.value.database.MarketDailyReportValue

interface MarketDailyReportService {

    fun list(query: MarketDailyReportValue.MarketDailyReportQuery): List<MarketDailyReport>

    fun batch(data: List<MarketDailyReportValue.MarketDailyReportCo>)


}
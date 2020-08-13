package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.MarketingDailyReport
import com.onepiece.gpgaming.beans.value.database.MarketingDailyReportValue

interface MarketingDailyReportService {

    fun list(query: MarketingDailyReportValue.MarketingDailyReportQuery): List<MarketingDailyReport>

    fun batch(data: List<MarketingDailyReportValue.MarketingDailyReportCo>)


}
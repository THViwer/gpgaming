package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.MarketingDailyReport
import com.onepiece.gpgaming.beans.value.database.MarketingDailyReportValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface MarketingDailyReportDao : BasicDao<MarketingDailyReport> {

    fun list(query: MarketingDailyReportValue.MarketingDailyReportQuery): List<MarketingDailyReport>

    fun batch(data: List<MarketingDailyReportValue.MarketingDailyReportCo>)

}
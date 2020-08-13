package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.MarketDailyReport
import com.onepiece.gpgaming.beans.value.database.MarketDailyReportValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface MarketDailyReportDao : BasicDao<MarketDailyReport> {

    fun list(query: MarketDailyReportValue.MarketDailyReportQuery): List<MarketDailyReport>

    fun batch(data: List<MarketDailyReportValue.MarketDailyReportCo>)

}
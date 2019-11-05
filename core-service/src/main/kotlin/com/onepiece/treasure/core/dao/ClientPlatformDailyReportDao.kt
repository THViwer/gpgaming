package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.ClientPlatformDailyReport
import com.onepiece.treasure.beans.value.database.ClientReportQuery
import com.onepiece.treasure.beans.value.internet.web.ClientReportVo
import java.time.LocalDate

interface ClientPlatformDailyReportDao  {

    fun create(reports: List<ClientPlatformDailyReport>)

    fun query(query: ClientReportQuery): List<ClientPlatformDailyReport>

    fun report(startDate: LocalDate, endDate: LocalDate): List<ClientReportVo>

}
package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.ClientPlatformDailyReport
import com.onepiece.treasure.beans.value.database.ClientPlatformDailyReportVo
import com.onepiece.treasure.beans.value.database.ClientReportQuery
import java.time.LocalDate

interface ClientPlatformDailyReportDao  {

    fun create(reports: List<ClientPlatformDailyReport>)

    fun query(query: ClientReportQuery): List<ClientPlatformDailyReport>

    fun report(startDate: LocalDate, endDate: LocalDate): List<ClientPlatformDailyReportVo>

}
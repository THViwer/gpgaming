package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.ClientPlatformDailyReport
import com.onepiece.gpgaming.beans.value.database.ClientPlatformDailyReportVo
import com.onepiece.gpgaming.beans.value.database.ClientReportQuery
import java.time.LocalDate

interface ClientPlatformDailyReportDao  {

    fun create(reports: List<ClientPlatformDailyReport>)

    fun query(query: ClientReportQuery): List<ClientPlatformDailyReport>

    fun report(startDate: LocalDate, endDate: LocalDate): List<ClientPlatformDailyReportVo>

}
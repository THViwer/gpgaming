package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.ClientDailyReport
import com.onepiece.treasure.beans.value.database.ClientReportQuery

interface ClientDailyReportService  {

    fun create(reports: List<ClientDailyReport>)

    fun query(query: ClientReportQuery): List<ClientDailyReport>

}
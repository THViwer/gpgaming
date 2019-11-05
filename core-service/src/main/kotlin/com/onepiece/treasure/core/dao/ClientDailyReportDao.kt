package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.ClientDailyReport
import com.onepiece.treasure.beans.value.database.ClientReportQuery

interface ClientDailyReportDao  {

    fun create(reports: List<ClientDailyReport>)

    fun query(query: ClientReportQuery): List<ClientDailyReport>

}
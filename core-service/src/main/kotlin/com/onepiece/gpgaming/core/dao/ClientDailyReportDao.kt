package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.value.database.ClientReportQuery

interface ClientDailyReportDao  {

    fun create(reports: List<ClientDailyReport>)

    fun query(query: ClientReportQuery): List<ClientDailyReport>

}
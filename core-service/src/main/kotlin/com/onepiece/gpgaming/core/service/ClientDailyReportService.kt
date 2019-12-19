package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.value.database.ClientReportQuery

interface ClientDailyReportService  {

    fun create(reports: List<ClientDailyReport>)

    fun query(query: ClientReportQuery): List<ClientDailyReport>

}
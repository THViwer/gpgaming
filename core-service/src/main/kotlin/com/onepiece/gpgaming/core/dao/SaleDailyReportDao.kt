package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.SaleDailyReport
import com.onepiece.gpgaming.beans.value.database.SaleDailyReportValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface SaleDailyReportDao: BasicDao<SaleDailyReport> {

    fun batch(data: List<SaleDailyReport>)

    fun list(query: SaleDailyReportValue.SaleDailyReportQuery): List<SaleDailyReport>

}
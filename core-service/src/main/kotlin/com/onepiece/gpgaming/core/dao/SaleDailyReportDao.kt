package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.SaleDailyReport
import com.onepiece.gpgaming.beans.model.SaleMonthReport
import com.onepiece.gpgaming.beans.value.database.SaleDailyReportValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao
import java.time.LocalDate

interface SaleDailyReportDao: BasicDao<SaleDailyReport> {

    fun batch(data: List<SaleDailyReport>)

    fun list(query: SaleDailyReportValue.SaleDailyReportQuery): List<SaleDailyReport>

    fun collect(startDate: LocalDate, endDate: LocalDate): List<SaleMonthReport>

}
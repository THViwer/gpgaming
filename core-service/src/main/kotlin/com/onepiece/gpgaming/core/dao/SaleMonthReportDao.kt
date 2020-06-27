package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.SaleMonthReport
import com.onepiece.gpgaming.beans.value.database.SaleMonthReportValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface SaleMonthReportDao: BasicDao<SaleMonthReport> {

    fun batch(data: List<SaleMonthReport>)

    fun list(query: SaleMonthReportValue.SaleMonthReportQuery): List<SaleMonthReport>

}
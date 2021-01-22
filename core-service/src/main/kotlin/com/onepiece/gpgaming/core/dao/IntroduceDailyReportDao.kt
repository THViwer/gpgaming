package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.IntroduceDailyReport
import com.onepiece.gpgaming.beans.value.database.IntroduceDailyReportValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface IntroduceDailyReportDao : BasicDao<IntroduceDailyReport> {

    fun batch(data: List<IntroduceDailyReport>)

    fun total(query: IntroduceDailyReportValue.IntroduceDailyReportQuery): List<IntroduceDailyReportValue.IntroduceDailyReportTotal>


}
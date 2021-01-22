package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.value.database.IntroduceDailyReportValue

interface IntroduceDailyReportService {

    fun total(query: IntroduceDailyReportValue.IntroduceDailyReportQuery): List<IntroduceDailyReportValue.IntroduceDailyReportTotal>

}
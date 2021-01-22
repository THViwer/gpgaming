package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.value.database.IntroduceDailyReportValue
import com.onepiece.gpgaming.core.dao.IntroduceDailyReportDao
import com.onepiece.gpgaming.core.service.IntroduceDailyReportService
import org.springframework.stereotype.Service


@Service
class IntroduceDailyReportServiceImpl(
        private val introduceDailyReportDao: IntroduceDailyReportDao
) : IntroduceDailyReportService {

    override fun total(query: IntroduceDailyReportValue.IntroduceDailyReportQuery): List<IntroduceDailyReportValue.IntroduceDailyReportTotal> {
        return introduceDailyReportDao.total(query = query)
    }
}
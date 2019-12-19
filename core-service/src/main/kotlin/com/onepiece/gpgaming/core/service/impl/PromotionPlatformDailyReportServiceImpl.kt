package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.PromotionPlatformDailyReport
import com.onepiece.gpgaming.beans.value.database.PromotionDailyReportValue
import com.onepiece.gpgaming.core.dao.PromotionPlatformDailyReportDao
import com.onepiece.gpgaming.core.service.PromotionPlatformDailyReportService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class PromotionPlatformDailyReportServiceImpl(
        private val promotionPlatformDailyReportDao: PromotionPlatformDailyReportDao
) : PromotionPlatformDailyReportService {


    override fun create(reports: List<PromotionPlatformDailyReport>) {
        return promotionPlatformDailyReportDao.create(reports)
    }

    override fun query(query: PromotionDailyReportValue.PlatformQuery): List<PromotionPlatformDailyReport> {
        return promotionPlatformDailyReportDao.query(query)
    }

    override fun statistical(startDate: LocalDate): List<PromotionDailyReportValue.StatisticalVo> {
        return promotionPlatformDailyReportDao.statistical(startDate)
    }
}
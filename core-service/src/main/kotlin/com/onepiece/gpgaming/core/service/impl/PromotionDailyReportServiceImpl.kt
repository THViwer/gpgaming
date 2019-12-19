package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.PromotionDailyReport
import com.onepiece.gpgaming.beans.value.database.PromotionDailyReportValue
import com.onepiece.gpgaming.core.dao.PromotionDailyReportDao
import com.onepiece.gpgaming.core.service.PromotionDailyReportService
import org.springframework.stereotype.Service

@Service
class PromotionDailyReportServiceImpl(
        private val promotionDailyReportDao: PromotionDailyReportDao
) : PromotionDailyReportService {


    override fun create(reports: List<PromotionDailyReport>) {
        return promotionDailyReportDao.create(reports)
    }

    override fun query(query: PromotionDailyReportValue.Query): List<PromotionDailyReport> {
        return promotionDailyReportDao.query(query)
    }

}
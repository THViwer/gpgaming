package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.model.PromotionDailyReport
import com.onepiece.treasure.beans.value.database.PromotionDailyReportValue
import com.onepiece.treasure.core.dao.PromotionDailyReportDao
import com.onepiece.treasure.core.service.PromotionDailyReportService
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
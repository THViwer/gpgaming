package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.PromotionDailyReport
import com.onepiece.treasure.beans.value.database.PromotionDailyReportValue

interface PromotionDailyReportService {

    fun create(reports: List<PromotionDailyReport>)

    fun query(query: PromotionDailyReportValue.Query): List<PromotionDailyReport>


}
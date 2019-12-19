package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.PromotionDailyReport
import com.onepiece.gpgaming.beans.value.database.PromotionDailyReportValue

interface PromotionDailyReportService {

    fun create(reports: List<PromotionDailyReport>)

    fun query(query: PromotionDailyReportValue.Query): List<PromotionDailyReport>


}
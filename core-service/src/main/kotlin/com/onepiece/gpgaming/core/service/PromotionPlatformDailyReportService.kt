package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.PromotionPlatformDailyReport
import com.onepiece.gpgaming.beans.value.database.PromotionDailyReportValue
import java.time.LocalDate

interface PromotionPlatformDailyReportService {

    fun create(reports: List<PromotionPlatformDailyReport>)

    fun query(query: PromotionDailyReportValue.PlatformQuery): List<PromotionPlatformDailyReport>

    fun statistical(startDate: LocalDate): List<PromotionDailyReportValue.StatisticalVo>

}
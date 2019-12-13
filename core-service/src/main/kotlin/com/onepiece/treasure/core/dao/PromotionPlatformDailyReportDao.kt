package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.PromotionPlatformDailyReport
import com.onepiece.treasure.beans.value.database.PromotionDailyReportValue
import com.onepiece.treasure.core.dao.basic.BasicDao
import java.time.LocalDate

interface PromotionPlatformDailyReportDao: BasicDao<PromotionPlatformDailyReport> {

    fun create(reports: List<PromotionPlatformDailyReport>)

    fun query(query: PromotionDailyReportValue.PlatformQuery): List<PromotionPlatformDailyReport>

    fun statistical(startDate: LocalDate): List<PromotionDailyReportValue.StatisticalVo>

}
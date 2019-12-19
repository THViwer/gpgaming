package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.PromotionPlatformDailyReport
import com.onepiece.gpgaming.beans.value.database.PromotionDailyReportValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao
import java.time.LocalDate

interface PromotionPlatformDailyReportDao: BasicDao<PromotionPlatformDailyReport> {

    fun create(reports: List<PromotionPlatformDailyReport>)

    fun query(query: PromotionDailyReportValue.PlatformQuery): List<PromotionPlatformDailyReport>

    fun statistical(startDate: LocalDate): List<PromotionDailyReportValue.StatisticalVo>

}
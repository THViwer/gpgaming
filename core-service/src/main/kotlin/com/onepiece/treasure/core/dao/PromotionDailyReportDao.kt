package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.PromotionDailyReport
import com.onepiece.treasure.beans.value.database.PromotionDailyReportValue
import com.onepiece.treasure.core.dao.basic.BasicDao

interface PromotionDailyReportDao: BasicDao<PromotionDailyReport> {

    fun create(reports: List<PromotionDailyReport>)

    fun query(query: PromotionDailyReportValue.Query): List<PromotionDailyReport>

}
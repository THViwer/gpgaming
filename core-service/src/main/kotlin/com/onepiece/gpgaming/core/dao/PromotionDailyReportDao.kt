package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.PromotionDailyReport
import com.onepiece.gpgaming.beans.value.database.PromotionDailyReportValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface PromotionDailyReportDao: BasicDao<PromotionDailyReport> {

    fun create(reports: List<PromotionDailyReport>)

    fun query(query: PromotionDailyReportValue.Query): List<PromotionDailyReport>

}
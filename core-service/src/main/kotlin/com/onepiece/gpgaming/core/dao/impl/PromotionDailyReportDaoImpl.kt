package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.PromotionDailyReport
import com.onepiece.gpgaming.beans.value.database.PromotionDailyReportValue
import com.onepiece.gpgaming.core.dao.PromotionDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class PromotionDailyReportDaoImpl : BasicDaoImpl<PromotionDailyReport>("promotion_daily_report"), PromotionDailyReportDao {

    override val mapper: (rs: ResultSet) -> PromotionDailyReport
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val day = rs.getString("day").let { LocalDate.parse(it) }
            val promotionId = rs.getInt("promotion_id")
            val promotionAmount = rs.getBigDecimal("promotion_amount")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }

            PromotionDailyReport(id = id, clientId = clientId, day = day, promotionId = promotionId, promotionAmount = promotionAmount,
                    createdTime = createdTime, status = status)
        }


    override fun create(reports: List<PromotionDailyReport>) {
        batchInsert(reports)
                .set("day")
                .set("client_id")
                .set("promotion_id")
                .set("promotion_amount")
                .execute { ps, entity ->
                    var x = 0
                    ps.setString(++x, "${entity.day}")
                    ps.setInt(++x, entity.clientId)
                    ps.setInt(++x, entity.promotionId)
                    ps.setBigDecimal(++x, entity.promotionAmount)
                }
    }

//    override fun create(report: PromotionDailyReport): Boolean {
//        return insert()
//                .set("day", report.day)
//                .set("client_id", report.clientId)
//                .set("platform", report.platform)
//                .set("promotion_id", report.promotionId)
//                .set("promotion_amount", report.promotionAmount)
//                .executeOnlyOne()
//    }

    override fun query(query: PromotionDailyReportValue.Query): List<PromotionDailyReport> {
        return query()
                .where("client_id", query.clientId)
                .asWhere("day >= ?", query.startDate)
                .asWhere("day < ?", query.endDate)
                .asWhere("promotion_id != -100")
                .sort("day desc")
                .execute(mapper)
    }

}
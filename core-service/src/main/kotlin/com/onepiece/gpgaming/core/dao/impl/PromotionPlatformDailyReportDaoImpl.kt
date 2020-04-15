package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.PromotionPlatformDailyReport
import com.onepiece.gpgaming.beans.value.database.PromotionDailyReportValue
import com.onepiece.gpgaming.core.dao.PromotionPlatformDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class PromotionPlatformDailyReportDaoImpl : BasicDaoImpl<PromotionPlatformDailyReport>("promotion_platform_daily_report"), PromotionPlatformDailyReportDao {

    override val mapper: (rs: ResultSet) -> PromotionPlatformDailyReport
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val day = rs.getString("day").let { LocalDate.parse(it) }
            val promotionId = rs.getInt("promotion_id")
            val promotionAmount = rs.getBigDecimal("promotion_amount")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val status = rs.getString("status").let { Status.valueOf(it) }

            PromotionPlatformDailyReport(id = id, clientId = clientId, day = day, promotionId = promotionId, promotionAmount = promotionAmount,
                    createdTime = createdTime, platform = platform, status = status)
        }


    override fun create(reports: List<PromotionPlatformDailyReport>) {
        batchInsert(reports)
                .set("day")
                .set("client_id")
                .set("platform")
                .set("promotion_id")
                .set("promotion_amount")
                .execute { ps, entity ->
                    var x = 0
                    ps.setString(++x, "${entity.day}")
                    ps.setInt(++x, entity.clientId)
                    ps.setString(++x, entity.platform.name)
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

    override fun query(query: PromotionDailyReportValue.PlatformQuery): List<PromotionPlatformDailyReport> {
        return query()
                .where("client_id", query.clientId)
                .where("promotion_id", query.promotionId)
                .asWhere("day >= ?", query.startDate)
                .asWhere("day < ?", query.endDate)
                .sort("day desc")
                .execute(mapper)
    }

    override fun statistical(startDate: LocalDate): List<PromotionDailyReportValue.StatisticalVo> {
        return query("client_id, promotion_id, sum(promotion_amount) as promotion_amount")
                .asWhere("day >= ?", startDate)
                .asWhere("day < ?", startDate.plusDays(1))
                .group("client_id, promotion_id")
                .execute { rs ->
                    val clientId = rs.getInt("client_id")
                    val promotionId = rs.getInt("promotion_id")
                    val promotionAmount = rs.getBigDecimal("promotion_amount")
                    PromotionDailyReportValue.StatisticalVo(clientId = clientId, promotionId = promotionId, promotionAmount = promotionAmount)
                }

    }
}
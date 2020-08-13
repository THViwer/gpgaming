package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.MarketingDailyReport
import com.onepiece.gpgaming.beans.value.database.MarketingDailyReportValue
import com.onepiece.gpgaming.core.dao.MarketingDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class MarketingDailyReportDaoImpl : MarketingDailyReportDao, BasicDaoImpl<MarketingDailyReport>("marketing_daily_report") {

    override val mapper: (rs: ResultSet) -> MarketingDailyReport
        get() = { rs ->
            val id = rs.getInt("id")
            val day = rs.getString("day").let { LocalDate.parse(it) }
            val marketingId = rs.getInt("marketing_id")
            val registerCount = rs.getInt("register_count")
            val viewCount = rs.getInt("view_count")
            val depositAmount = rs.getBigDecimal("deposit_amount")
            val withdrawAmount = rs.getBigDecimal("withdraw_amount")
            val bet = rs.getBigDecimal("bet")

            MarketingDailyReport(id = id, day = day, marketingId = marketingId, registerCount = registerCount, viewCount = viewCount,
                    depositAmount = depositAmount, withdrawAmount = withdrawAmount, bet = bet)
        }

    override fun list(query: MarketingDailyReportValue.MarketingDailyReportQuery): List<MarketingDailyReport> {
        return query()
                .where("client_id", query.clientId)
                .asWhere("day >= ?", query.startDate)
                .asWhere("day <= ?", query.endDate)
                .execute(mapper)
    }

    override fun batch(data: List<MarketingDailyReportValue.MarketingDailyReportCo>) {
        batchInsert(data)
                .set("day")
                .set("marketing_id")
                .set("register_count")
                .set("view_count")
                .set("deposit_amount")
                .set("withdraw_amount")
                .set("bet")
                .execute { ps, entity ->
                    var x = 0
                    ps.setString(++x, entity.day.toString())
                    ps.setInt(++x, entity.marketingId)
                    ps.setInt(++x, entity.registerCount)
                    ps.setInt(++x, entity.viewCount)
                    ps.setBigDecimal(++x, entity.depositAmount)
                    ps.setBigDecimal(++x, entity.withdrawAmount)
                    ps.setBigDecimal(++x, entity.bet)
                }
    }


}
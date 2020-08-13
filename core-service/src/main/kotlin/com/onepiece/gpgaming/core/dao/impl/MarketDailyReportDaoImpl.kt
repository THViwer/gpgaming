package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.MarketDailyReport
import com.onepiece.gpgaming.beans.value.database.MarketDailyReportValue
import com.onepiece.gpgaming.core.dao.MarketDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class MarketDailyReportDaoImpl : MarketDailyReportDao, BasicDaoImpl<MarketDailyReport>("market_daily_report") {

    override val mapper: (rs: ResultSet) -> MarketDailyReport
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val day = rs.getString("day").let { LocalDate.parse(it) }
            val marketId = rs.getInt("market_id")
            val registerCount = rs.getInt("register_count")
            val viewCount = rs.getInt("view_count")
            val depositAmount = rs.getBigDecimal("deposit_amount")
            val withdrawAmount = rs.getBigDecimal("withdraw_amount")
            val bet = rs.getBigDecimal("bet")

            MarketDailyReport(id = id, day = day, clientId = clientId, marketId = marketId, registerCount = registerCount,
                    viewCount = viewCount, depositAmount = depositAmount, withdrawAmount = withdrawAmount, bet = bet)
        }

    override fun list(query: MarketDailyReportValue.MarketDailyReportQuery): List<MarketDailyReport> {
        return query()
                .where("client_id", query.clientId)
                .asWhere("day >= ?", query.startDate)
                .asWhere("day <= ?", query.endDate)
                .execute(mapper)
    }

    override fun batch(data: List<MarketDailyReportValue.MarketDailyReportCo>) {
        batchInsert(data)
                .set("client_id")
                .set("day")
                .set("market_id")
                .set("register_count")
                .set("view_count")
                .set("deposit_amount")
                .set("withdraw_amount")
                .set("bet")
                .execute { ps, entity ->
                    var x = 0
                    ps.setInt(++x, entity.clientId)
                    ps.setString(++x, entity.day.toString())
                    ps.setInt(++x, entity.marketId)
                    ps.setInt(++x, entity.registerCount)
                    ps.setInt(++x, entity.viewCount)
                    ps.setBigDecimal(++x, entity.depositAmount)
                    ps.setBigDecimal(++x, entity.withdrawAmount)
                    ps.setBigDecimal(++x, entity.bet)
                }
    }


}
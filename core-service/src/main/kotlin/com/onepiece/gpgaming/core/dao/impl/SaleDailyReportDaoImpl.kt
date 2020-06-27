package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.SaleDailyReport
import com.onepiece.gpgaming.beans.value.database.SaleDailyReportValue
import com.onepiece.gpgaming.core.dao.SaleDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class SaleDailyReportDaoImpl : BasicDaoImpl<SaleDailyReport>("sale_daily_report"), SaleDailyReportDao {

    override val mapper: (rs: ResultSet) -> SaleDailyReport
        get() = { rs ->
            val id = rs.getInt("id")
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val saleId = rs.getInt("sale_id")
            val day = rs.getDate("day").toLocalDate()
            val saleUsername = rs.getString("sale_username")
            val ownCustomerScale = rs.getBigDecimal("own_customer_scale")
            val ownCustomerFee = rs.getBigDecimal("own_customer_fee")
            val systemCustomerScale = rs.getBigDecimal("own_customer_scale")
            val systemCustomerFee = rs.getBigDecimal("system_customer_fee")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            SaleDailyReport(id = id, bossId = bossId, clientId = clientId, saleId = saleId, day = day, saleUsername = saleUsername,
                    ownCustomerScale = ownCustomerScale, ownCustomerFee = ownCustomerFee, systemCustomerScale = systemCustomerScale,
                    systemCustomerFee = systemCustomerFee, createdTime = createdTime)
        }

    override fun batch(data: List<SaleDailyReport>) {

        batchInsert(data)
                .set("boss_id")
                .set("client_id")
                .set("sale_id")
                .set("day")
                .set("sale_username")
                .set("own_customer_scale")
                .set("own_customer_fee")
                .set("system_customer_scale")
                .set("system+customer_fee")
                .execute { ps, entity ->
                    var x = 0
                    ps.setInt(++x, entity.bossId)
                    ps.setInt(++x, entity.clientId)
                    ps.setInt(++x, entity.saleId)
                    ps.setString(++x, entity.day.toString())
                    ps.setString(++x, entity.saleUsername)
                    ps.setBigDecimal(++x, entity.ownCustomerScale)
                    ps.setBigDecimal(++x, entity.ownCustomerFee)
                    ps.setBigDecimal(++x, entity.systemCustomerScale)
                    ps.setBigDecimal(++x, entity.systemCustomerFee)
                }
    }

    override fun list(query: SaleDailyReportValue.SaleDailyReportQuery): List<SaleDailyReport> {

        return query()
                .where("boss_id", query.bossId)
                .where("client_id", query.clientId)
                .where("member_id", query.memberId)
                .where("sale_id", query.saleId)
                .asWhere("day >= ?", query.startDate)
                .asWhere("day < ?", query.endDate)
                .execute(mapper)
    }
}
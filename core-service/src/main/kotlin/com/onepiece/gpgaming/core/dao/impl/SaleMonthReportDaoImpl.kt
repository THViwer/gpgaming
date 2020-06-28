package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.SaleMonthReport
import com.onepiece.gpgaming.beans.value.database.SaleMonthReportValue
import com.onepiece.gpgaming.core.dao.SaleMonthReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class SaleMonthReportDaoImpl : BasicDaoImpl<SaleMonthReport>("sale_month_report"), SaleMonthReportDao {

    override val mapper: (rs: ResultSet) -> SaleMonthReport
        get() = { rs ->
            val id = rs.getInt("id")
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val saleId = rs.getInt("sale_id")
            val day = rs.getDate("day").toLocalDate()
            val saleUsername = rs.getString("sale_username")

            val ownTotalDeposit = rs.getBigDecimal("own_total_deposit")
            val ownTotalWithdraw = rs.getBigDecimal("own_total_withdraw")
            val ownTotalPromotion = rs.getBigDecimal("own_total_promotion")
            val ownTotalRebate = rs.getBigDecimal("own_total_rebate")
            val ownCustomerScale = rs.getBigDecimal("own_customer_scale")
            val ownCustomerFee = rs.getBigDecimal("own_customer_fee")

            val systemTotalDeposit = rs.getBigDecimal("system_total_deposit")
            val systemTotalWithdraw = rs.getBigDecimal("system_total_withdraw")
            val systemTotalPromotion = rs.getBigDecimal("system_total_promotion")
            val systemTotalRebate = rs.getBigDecimal("system_total_rebate")
            val systemCustomerScale = rs.getBigDecimal("system_customer_scale")
            val systemCustomerFee = rs.getBigDecimal("system_customer_fee")


            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            SaleMonthReport(id = id, bossId = bossId, clientId = clientId, saleId = saleId, day = day, saleUsername = saleUsername,
                    ownTotalDeposit = ownTotalDeposit, ownTotalWithdraw = ownTotalWithdraw, ownTotalPromotion = ownTotalPromotion, ownTotalRebate = ownTotalRebate,
                    ownCustomerFee = ownCustomerFee, ownCustomerScale = ownCustomerScale, systemCustomerFee = systemCustomerFee, systemCustomerScale = systemCustomerScale,
                    systemTotalDeposit = systemTotalDeposit, systemTotalWithdraw = systemTotalWithdraw, systemTotalPromotion = systemTotalPromotion, systemTotalRebate = systemTotalRebate,
                    createdTime = createdTime)
        }

    override fun batch(data: List<SaleMonthReport>) {

        batchInsert(data)
                .set("boss_id")
                .set("client_id")
                .set("sale_id")
                .set("day")
                .set("sale_username")

                .set("own_total_deposit")
                .set("own_total_withdraw")
                .set("own_total_promotion")
                .set("own_total_rebate")
                .set("own_customer_scale")
                .set("own_customer_fee")


                .set("system_total_deposit")
                .set("system_total_withdraw")
                .set("system_total_promotion")
                .set("system_total_rebate")
                .set("system_customer_scale")
                .set("system_customer_fee")

                .execute { ps, entity ->
                    var x = 0
                    ps.setInt(++x, entity.bossId)
                    ps.setInt(++x, entity.clientId)
                    ps.setInt(++x, entity.saleId)
                    ps.setString(++x, entity.day.toString())
                    ps.setString(++x, entity.saleUsername)

                    ps.setBigDecimal(++x, entity.ownTotalDeposit)
                    ps.setBigDecimal(++x, entity.ownTotalWithdraw)
                    ps.setBigDecimal(++x, entity.ownTotalPromotion)
                    ps.setBigDecimal(++x, entity.ownTotalRebate)
                    ps.setBigDecimal(++x, entity.ownCustomerScale)
                    ps.setBigDecimal(++x, entity.ownCustomerFee)

                    ps.setBigDecimal(++x, entity.systemTotalDeposit)
                    ps.setBigDecimal(++x, entity.systemTotalWithdraw)
                    ps.setBigDecimal(++x, entity.systemTotalPromotion)
                    ps.setBigDecimal(++x, entity.systemTotalRebate)
                    ps.setBigDecimal(++x, entity.systemCustomerScale)
                    ps.setBigDecimal(++x, entity.systemCustomerFee)
                }
    }

    override fun list(query: SaleMonthReportValue.SaleMonthReportQuery): List<SaleMonthReport> {

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
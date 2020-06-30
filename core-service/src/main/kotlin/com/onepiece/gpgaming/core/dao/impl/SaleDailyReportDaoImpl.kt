package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.SaleDailyReport
import com.onepiece.gpgaming.beans.model.SaleMonthReport
import com.onepiece.gpgaming.beans.value.database.SaleDailyReportValue
import com.onepiece.gpgaming.core.dao.SaleDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalDateTime

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

            SaleDailyReport(id = id, bossId = bossId, clientId = clientId, saleId = saleId, day = day, saleUsername = saleUsername,
                    ownTotalDeposit = ownTotalDeposit, ownTotalWithdraw = ownTotalWithdraw, ownTotalPromotion = ownTotalPromotion, ownTotalRebate = ownTotalRebate,
                    ownCustomerFee = ownCustomerFee, ownCustomerScale = ownCustomerScale, systemCustomerFee = systemCustomerFee, systemCustomerScale = systemCustomerScale,
                    systemTotalDeposit = systemTotalDeposit, systemTotalWithdraw = systemTotalWithdraw, systemTotalPromotion = systemTotalPromotion, systemTotalRebate = systemTotalRebate,
                    createdTime = createdTime)
        }

    override fun batch(data: List<SaleDailyReport>) {

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

    override fun list(query: SaleDailyReportValue.SaleDailyReportQuery): List<SaleDailyReport> {

        return query()
                .where("boss_id", query.bossId)
                .where("client_id", query.clientId)
                .where("member_id", query.memberId)
                .where("sale_id", query.saleId)
                .where("sale_username", query.saleUsername)
                .asWhere("day >= ?", query.startDate)
                .asWhere("day < ?", query.endDate)
                .execute(mapper)
    }

    override fun collect(startDate: LocalDate, endDate: LocalDate): List<SaleMonthReport> {

        val sql = """
            select 
                boss_id,
                client_id,
                sale_id,
                sale_username,
                sum(own_total_deposit) own_total_deposit,
                sum(own_total_withdraw) own_total_withdraw,
                sum(own_total_promotion) own_total_promotion,
                sum(own_total_rebate) own_total_rebate,
                sum(own_customer_scale) own_customer_scale,
                sum(own_customer_fee) own_customer_fee,
                sum(system_total_deposit) system_total_deposit,
                sum(system_total_withdraw) system_total_withdraw,
                sum(system_total_promotion) system_total_promotion,
                sum(system_total_rebate) system_total_rebate,
                sum(system_customer_scale) system_customer_scale,
                sum(system_customer_fee) system_customer_fee
            from sale_daily_report 
            where day >= '$startDate' and day < '$endDate' 
            group by boss_id, client_id, sale_id, sale_username;
        """.trimIndent()

        return jdbcTemplate.query(sql) { rs, _ ->

            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val saleId = rs.getInt("sale_id")
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

            SaleMonthReport(bossId = bossId, clientId = clientId, saleId = saleId, saleUsername = saleUsername,
                    ownTotalDeposit = ownTotalDeposit, ownTotalWithdraw = ownTotalWithdraw, ownTotalPromotion = ownTotalPromotion, ownTotalRebate = ownTotalRebate,
                    ownCustomerScale = ownCustomerScale, ownCustomerFee = ownCustomerFee, systemCustomerScale = systemCustomerScale, systemCustomerFee = systemCustomerFee,
                    systemTotalDeposit = systemTotalDeposit, systemTotalWithdraw = systemTotalWithdraw, systemTotalPromotion = systemTotalPromotion, systemTotalRebate = systemTotalRebate,
                    id = -1, day = startDate, createdTime = LocalDateTime.now())
        }

    }
}
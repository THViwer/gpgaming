package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.IntroduceDailyReport
import com.onepiece.gpgaming.beans.value.database.IntroduceDailyReportValue
import com.onepiece.gpgaming.core.dao.IntroduceDailyReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class IntroduceDailyReportDaoImpl : BasicDaoImpl<IntroduceDailyReport>("introduce_daily_report"), IntroduceDailyReportDao {

    override fun batch(data: List<IntroduceDailyReport>) {
        batchInsert(data = data)
                .set("client_id")
                .set("member_id")
                .set("username")
                .set("day")
                .set("register_count")
                .set("first_deposit_count")
                .set("commissions")
                .execute { ps, entity ->
                    var x = 0
                    ps.setInt(++x, entity.clientId)
                    ps.setInt(++x, entity.memberId)
                    ps.setString(++x, entity.username)
                    ps.setString(++x, entity.day.toString())
                    ps.setInt(++x, entity.registerCount)
                    ps.setInt(++x, entity.firstDepositCount)
                    ps.setBigDecimal(++x, entity.commissions)

                }
    }

    override fun total(query: IntroduceDailyReportValue.IntroduceDailyReportQuery): List<IntroduceDailyReportValue.IntroduceDailyReportTotal> {
        val day = "${query.startDate}~${query.endDate}"
        return query("client_id, member_id, username, sum(register_count) register_count, sum(first_deposit_count) first_deposit_count, sum(commissions) commissions")
                .where("client_id", query.clientId)
                .asWhere("day > ?", query.startDate)
                .asWhere("day <= ?", query.endDate)
                .group("client_id, member_id, username")
                .execute { rs ->

                    val clientId = rs.getInt("client_id")
                    val memberId = rs.getInt("member_id")
                    val username = rs.getString("username")
                    val registerCount = rs.getInt("register_count")
                    val firstDepositCount = rs.getInt("first_deposit_count")
                    val commissions = rs.getBigDecimal("commissions")


                    IntroduceDailyReportValue.IntroduceDailyReportTotal(day = day, clientId = clientId, memberId = memberId, registerCount = registerCount,
                            firstDepositCount = firstDepositCount, commissions = commissions, username = username)

                }
    }

    override val mapper: (rs: ResultSet) -> IntroduceDailyReport
        get() = { rs ->
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val username = rs.getString("username")
            val day = rs.getString("day").let {
                LocalDate.parse(it)
            }
            val registerCount = rs.getInt("register_count")
            val firstDepositCount = rs.getInt("first_deposit_count")
            val commissions = rs.getBigDecimal("commissions")

            IntroduceDailyReport(clientId = clientId, memberId = memberId, day = day, registerCount = registerCount,
                    firstDepositCount = firstDepositCount, commissions = commissions, username = username)
        }
}
package com.onepiece.gpgaming.task.introduce.util

import com.onepiece.gpgaming.beans.model.IntroduceDailyReport
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.utils.JdbcBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class IntroduceReportUtil(
        private val jdbcTemplate: JdbcTemplate,
        private val memberService: MemberService
) {

    fun startDailyReport(startDate: LocalDate): List<IntroduceDailyReport> {

        val map = hashMapOf<Int, IntroduceDailyReport>()

        this.getRegisterCount(startDate = startDate, map = map)
        this.getFirstDepositCount(startDate = startDate, map = map)
        this.getCommissions(startDate = startDate, map = map)

        val data = map.map { it.value }

        val memberIds = data.map { it.memberId }
        val members = memberService.findByIds(ids = memberIds).map { it.id to it }.toMap()

        return data.map {
            val username = members[it.memberId]?.username ?: ""
            it.copy(username = username)
        }

    }


    private fun getRegisterCount(startDate: LocalDate, map: HashMap<Int, IntroduceDailyReport>) {

        /**
         * -- 业主id、介绍人id、注册人数
         * select client_id, introduce_id, count(*) count from member where created_time > '2021-01-01' and introduce_id > 0 group by client_id, introduce_id;
         */

        val list = JdbcBuilder.query(jdbcTemplate = jdbcTemplate, table = "member", returnColumns = "client_id, introduce_id, count(*) count")
                .asWhere("created_time > ?", startDate)
                .asWhere("introduce_id > 0")
                .group("client_id, introduce_id")
                .execute { rs ->

                    val clientId = rs.getInt("client_id")
                    val memberId = rs.getInt("introduce_id")
                    val registerCount = rs.getInt("count")

                    val report = map[memberId] ?: IntroduceDailyReport.empty(clientId = clientId, memberId = memberId)

                    report.copy(registerCount = registerCount)
                }

        list.forEach {
            map[it.memberId] = it
        }
    }

    private fun getFirstDepositCount(startDate: LocalDate, map: HashMap<Int, IntroduceDailyReport>) {
        /**
         * -- 业主id、介绍人id、首充人数
         * select client_id, introduce_id, count(*) first_deposit_count from member where first_deposit_day > '2020-01-01' and introduce_id > 0 group by client_id, introduce_id;
         */

        val list = JdbcBuilder.query(jdbcTemplate = jdbcTemplate, table = "member", returnColumns = "client_id, introduce_id, count(*) first_deposit_count")
                .asWhere("first_deposit_day > ?", startDate)
                .asWhere("introduce_id > 0")
                .group("client_id, introduce_id")
                .execute { rs ->

                    val clientId = rs.getInt("client_id")
                    val memberId = rs.getInt("member_id")
                    val firstDepositCount = rs.getInt("first_deposit_count")

                    val report = map[memberId] ?: IntroduceDailyReport.empty(clientId = clientId, memberId = memberId)

                    report.copy(firstDepositCount = firstDepositCount)
                }

        list.forEach {
            map[it.memberId] = it
        }
    }

    private fun getCommissions(startDate: LocalDate, map: HashMap<Int, IntroduceDailyReport>) {

        /**
         *  -- 业主id、介绍人id、佣金
         * select client_id, member_id, sum(money) commissions from wallet_note where `event` in ('INTRODUCE', 'INTRODUCE_DEPOSIT_COMMISSION', 'INTRODUCE_REGISTER_COMMISSION') and created_time > '2021-01-01' group by client_id, member_id;
         */
        val list = JdbcBuilder.query(jdbcTemplate = jdbcTemplate, table = "wallet_note", returnColumns = "client_id, member_id, sum(money) commissions")
                .asWhere("created_time > ?", startDate)
                .whereIn("event", listOf("INTRODUCE", "INTRODUCE_DEPOSIT_COMMISSION", "INTRODUCE_REGISTER_COMMISSION"))
                .group("client_id, member_id")
                .execute { rs ->

                    val clientId = rs.getInt("client_id")
                    val memberId = rs.getInt("member_id")
                    val commissions = rs.getBigDecimal("commissions")

                    val report = map[memberId] ?: IntroduceDailyReport.empty(clientId = clientId, memberId = memberId)

                    report.copy(commissions = commissions)
                }

        list.forEach {
            map[it.memberId] = it
        }

    }


}


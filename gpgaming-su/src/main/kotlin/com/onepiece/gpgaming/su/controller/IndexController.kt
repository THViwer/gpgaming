package com.onepiece.gpgaming.su.controller

import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import springfox.documentation.annotations.ApiIgnore
import java.time.LocalDate

@Controller
class IndexController(
        private val jdbcTemplate: JdbcTemplate
) {

    @GetMapping("/")
    @ApiIgnore
    fun index(): String? {
        return "redirect:swagger-ui.html"
    }

    @GetMapping("/cabbage", produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @ResponseBody
    fun collect(): Any {

        val today = LocalDate.now()

        val startMonth = today.withDayOfMonth(1)
        val endMonth = startMonth.plusMonths(1)

        val data = (-2..12).filter { x ->
            val start = startMonth.minusMonths(x.toLong())
            "$start" > "2020-05-01"
        }.map { x ->

            when (x) {
                -2 -> {
                    val list = this.todayDetails(today)
                    "今日详情" to list
                }
                -1 -> {
                    val list = this.report(startDate = today, endDate = today.plusDays(1))
                    "今日" to list
                }
                else -> {
                    val start = startMonth.minusMonths(x.toLong())
                    val end = endMonth.minusMonths(x.toLong())

                    val list =  this.report(startDate = start, endDate = end)

                    "${start.month.value}月" to list
                }
            }
        }.toMap()
        return data
    }

    private fun todayDetails(startDate: LocalDate): List<String> {
        val s1 = "select * from deposit where state = 'Successful' and created_time > '$startDate';"
        val s2 = "select * from pay_order where state = 'Successful' and created_time > '$startDate';"
        val s3 = "select * from withdraw where state = 'Successful' and created_time > '$startDate';"

        val d1 = jdbcTemplate.query(s1) { rs, _ ->
            val clientId = rs.getInt("client_id")
            val username = rs.getString("username")
            val money = rs.getBigDecimal("money")
            "银行转账 => 业主Id:${clientId}, 用户：${username}, 充值金额：${money}"
        }

        val d2 = jdbcTemplate.query(s2) { rs, _ ->
            val clientId = rs.getInt("client_id")
            val username = rs.getString("username")
            val money = rs.getBigDecimal("money")
            "三方充值 => 业主Id:${clientId}, 用户：${username}, 充值金额：${money}"
        }

        val d3 = jdbcTemplate.query(s3) { rs, _ ->
            val clientId = rs.getInt("client_id")
            val username = rs.getString("username")
            val money = rs.getBigDecimal("money")
            "取款 => 业主Id:${clientId}, 用户：${username}, 取款金额：${money}"
        }

        return d1.plus(d2).plus(d3)
    }

    private fun report(startDate: LocalDate, endDate: LocalDate): List<String> {
        val s1 = "select client_id, sum(money) amount from deposit where state = 'Successful' and created_time > '${startDate}' and created_time < '${endDate}' group by client_id;"
        val s2 = "select client_id, sum(amount) amount from pay_order where state = 'Successful' and created_time > '${startDate}' and created_time < '${endDate}' group by client_id;"
        val s3 = "select client_id, sum(money) amount from withdraw where state = 'Successful' and created_time > '${startDate}' and created_time < '${endDate}' group by client_id;"

        val d1 = jdbcTemplate.query(s1) { rs, _ ->
            val clientId = rs.getInt("client_id")
            val amount = rs.getBigDecimal("amount")
            "银行转账 => 业主：${clientId}, 充值金额：${amount}"
        }

        val d2 = jdbcTemplate.query(s2) { rs, _ ->
            val clientId = rs.getInt("client_id")
            val amount = rs.getBigDecimal("amount")
            "三方充值 => 业主：${clientId}, 充值金额：${amount}"
        }

        val d3 = jdbcTemplate.query(s3) { rs, _ ->
            val clientId = rs.getInt("client_id")
            val amount = rs.getBigDecimal("amount")
            "取款 => 业主：${clientId}, 取款金额：${amount}"
        }

        return d1.plus(d2).plus(d3)
    }



}
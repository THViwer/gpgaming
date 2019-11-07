package com.onepiece.treasure.games.slot.kiss918

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.beans.model.token.Kiss918ClientToken
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.value.ReportVo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class Kiss918ApiService(
        private val okHttpUtil: OkHttpUtil
) : Kiss918Api {

    private val log = LoggerFactory.getLogger(Kiss918ApiService::class.java)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun addUser(token: Kiss918ClientToken, password: String): String {

        val agentName = token.appId

        val url = Kiss918Build.instance(path = "/ashx/account/account.ashx")
                .set("loginUser", agentName)
                .set("userName", agentName)
                .set("UserAreaId", "1")
                .set("action", "RandomUserName")
                .build(agentName, token, agentName)

        val result = okHttpUtil.doGet(url, Kiss918Value.RegisterUsernameResult::class.java)
        log.info("generator username result: $result")
        check(result.success) { OnePieceExceptionCode.PLATFORM_METHOD_FAIL }

        val generatorUsername = result.account

        val addPlayerUrl = Kiss918Build.instance(path = "/ashx/account/account.ashx")
                .set("action", "AddUser")
                .set("agent", agentName)
                .set("PassWd", password)
                .set("userName", generatorUsername)
                .set("Name", generatorUsername)
                .set("tel", "1234124141241")
                .set("Memo", "-")
                .set("UserType", "1")
                .set("UserAreaId", "1")// number
                .set("pwdtype", "1")
                .build(username = generatorUsername, token = token)
        val addPlayerResult = okHttpUtil.doGet(addPlayerUrl, String::class.java)
        log.info("add player result : $addPlayerResult")

        return generatorUsername

    }

    override fun userinfo(token: Kiss918ClientToken, username: String): BigDecimal {

        val url = Kiss918Build.instance(path = "/ashx/account/account.ashx")
                .set("action", "getUserInfo")
                .set("userName", username)
                .build(username = username, token = token)

        val userinfo = okHttpUtil.doGet(url, Kiss918Value.Userinfo::class.java)
        return userinfo.moneyNumber


    }

    override fun setScore(token: Kiss918ClientToken, orderId: String, username: String, amount: BigDecimal): String {
        val url = Kiss918Build.instance(path = "/ashx/account/setScore.ashx")
                .set("action", "setServerScore")
                .set("orderid", orderId)
                .set("scoreNum", "$amount")
                .set("userName", username)
                .set("ActionUser", "system")
                .set("ActionIp", "12.213.1.24")
                .build(token = token, username = username)
        val result = okHttpUtil.doGet(url, Kiss918Value.TransferResult::class.java)
        return orderId
    }

    override fun gameLog(token: Kiss918ClientToken, username: String, startTime: LocalDateTime, endTime: LocalDateTime): Any {
        val url = Kiss918Build.instance(GameConstant.KISS918_API_ORDER_URL, path = "/ashx/GameLog.ashx")
                .set("pageIndex", "1")
                .set("pageSize", "1000")
                .set("userName", username)
                .set("sDate", startTime.format(dateTimeFormatter))
                .set("eDate", endTime.format(dateTimeFormatter))
                .build(token = token, username = username)

        val result = okHttpUtil.doGet(url, String::class.java)
        log.info("game log : $result")

        return result

    }

    override fun accountReport(token: Kiss918ClientToken, username: String, startDate: LocalDate, endDate: LocalDate): List<ReportVo> {

        val url = Kiss918Build.instance(domain = GameConstant.KISS918_API_ORDER_URL, path = "/ashx/AccountReport.ashx")
                .set("userName", username)
                .set("sDate", startDate.toString())
                .set("eDate", endDate.toString())
                .build(token = token, username = username)
        val result = okHttpUtil.doGet(url, Kiss918Value.ReportResult::class.java)
        log.info("member report: $result")

        return result.results.map {
            ReportVo(day = it.myDate, win = it.win, bet = it.press)
        }
    }

    override fun agentMoneyLog(token: Kiss918ClientToken, startDate: LocalDate, endDate: LocalDate): List<ReportVo> {
        val url = Kiss918Build.instance(domain = GameConstant.KISS918_API_ORDER_URL, path = "/ashx/AgentMoneyLog.ashx")
                .set("userName", token.appId)
                .set("sDate", startDate.toString())
                .set("eDate", endDate.toString())
                .build(token = token, username = token.appId)
        val result = okHttpUtil.doGet(url, Kiss918Value.ReportResult::class.java)
        log.info("member report: $result")
        return result.results.map {
            ReportVo(day = it.myDate, win = it.win, bet = it.press)
        }
    }
}
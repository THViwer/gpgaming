package com.onepiece.treasure.games.slot.kiss918

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.Kiss918ClientToken
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.value.ReportVo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 说明
 * 支持用户区域：
 *  1	马来
 *  2	泰国
 *  3	柬埔寨
 *  4	缅甸
 *  5	中国
 *  6	越南
 *  7	印尼
 */
@Service
class Kiss918Service(
        private val okHttpUtil: OkHttpUtil
) : PlatformApi() {

    private val log = LoggerFactory.getLogger(Kiss918Service::class.java)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun register(registerReq: GameValue.RegisterReq): String {
        val agentName = (registerReq.token as Kiss918ClientToken).appId

        val url = Kiss918Build.instance(path = "/ashx/account/account.ashx")
                .set("loginUser", agentName)
                .set("userName", agentName)
                .set("UserAreaId", "1")
                .set("action", "RandomUserName")
                .build(agentName, registerReq.token, agentName)

        val result = okHttpUtil.doGet(url, Kiss918Value.RegisterUsernameResult::class.java)
        log.info("generator username result: $result")
        check(result.success) { OnePieceExceptionCode.PLATFORM_METHOD_FAIL }

        val generatorUsername = result.account

        val addPlayerUrl = Kiss918Build.instance(path = "/ashx/account/account.ashx")
                .set("action", "AddUser")
                .set("agent", agentName)
                .set("PassWd", registerReq.password)
                .set("userName", generatorUsername)
                .set("Name", generatorUsername)
                .set("tel", "1234124141241")
                .set("Memo", "-")
                .set("UserType", "1")
                .set("UserAreaId", "1")// number
                .set("pwdtype", "1")
                .build(username = generatorUsername, token = registerReq.token)
        val addPlayerResult = okHttpUtil.doGet(addPlayerUrl, String::class.java)
        log.info("add player result : $addPlayerResult")

        return generatorUsername
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {

        val url = Kiss918Build.instance(path = "/ashx/account/account.ashx")
                .set("action", "getUserInfo")
                .set("userName", balanceReq.username)
                .build(username = balanceReq.username, token = balanceReq.token as Kiss918ClientToken)

        val userInfo = okHttpUtil.doGet(url, Kiss918Value.Userinfo::class.java)
        return userInfo.moneyNumber
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        val url = Kiss918Build.instance(path = "/ashx/account/setScore.ashx")
                .set("action", "setServerScore")
                .set("orderid", transferReq.orderId)
                .set("scoreNum", "${transferReq.amount}")
                .set("userName", transferReq.username)
                .set("ActionUser", "system")
                .set("ActionIp", "12.213.1.24")
                .build(token = transferReq.token as Kiss918ClientToken, username = transferReq.username)
        val result = okHttpUtil.doGet(url, Kiss918Value.TransferResult::class.java)
        return transferReq.orderId
    }

    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): Any {

        val url = Kiss918Build.instance(GameConstant.KISS918_API_ORDER_URL, path = "/ashx/GameLog.ashx")
                .set("pageIndex", "1")
                .set("pageSize", "1000")
                .set("userName", betOrderReq.username)
                .set("sDate", betOrderReq.startTime.format(dateTimeFormatter))
                .set("eDate", betOrderReq.endTime.format(dateTimeFormatter))
                .build(token = betOrderReq.token as Kiss918ClientToken, username = betOrderReq.username)

        val result = okHttpUtil.doGet(url, String::class.java)
        log.info("game log : $result")
        return result
    }

    open fun memberReport(token: Kiss918ClientToken, username: String, startDate: LocalDate, endDate: LocalDate): List<ReportVo> {

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

    open fun clientReport(token: Kiss918ClientToken, startDate: LocalDate, endDate: LocalDate): List<ReportVo> {
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
package com.onepiece.treasure.games.slot.pussy888

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.Pussy888ClientToken
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.http.OkHttpUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@Service
class Pussy888Service(
        private val okHttpUtil: OkHttpUtil
) : PlatformApi() {

    private val log = LoggerFactory.getLogger(Pussy888Service::class.java)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun register(registerReq: GameValue.RegisterReq): String {
        val token = registerReq.token as Pussy888ClientToken

        val agentName = token.appId

        val url = Pussy888Build.instance(path = "/ashx/account/account.ashx")
                .set("loginUser", agentName)
                .set("userName", agentName)
                .set("UserAreaId", "1")
                .set("action", "RandomUserName")
                .build(agentName, token, agentName)

        val result = okHttpUtil.doGet(url, Pussy888Value.RegisterUsernameResult::class.java)
        log.info("generator username result: $result")
        check(result.success) { OnePieceExceptionCode.PLATFORM_METHOD_FAIL }

        val generatorUsername = result.account

        val addPlayerUrl = Pussy888Build.instance(path = "/ashx/account/account.ashx")
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
                .build(username = generatorUsername, token = token)
        val addPlayerResult = okHttpUtil.doGet(addPlayerUrl, String::class.java)
        log.info("add player result : $addPlayerResult")

        return generatorUsername
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val url = Pussy888Build.instance(path = "/ashx/account/account.ashx")
                .set("action", "getUserInfo")
                .set("userName", balanceReq.username)
                .build(username = balanceReq.username, token = balanceReq.token as Pussy888ClientToken)

        val userInfo = okHttpUtil.doGet(url, Pussy888Value.Userinfo::class.java)
        return userInfo.moneyNumber

    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        val url = Pussy888Build.instance(path = "/ashx/account/setScore.ashx")
                .set("action", "setServerScore")
                .set("orderid", transferReq.orderId)
                .set("scoreNum", "${transferReq.amount}")
                .set("userName", transferReq.username)
                .set("ActionUser", "system")
                .set("ActionIp", "12.213.1.24")
                .build(token = transferReq.token as Pussy888ClientToken, username = transferReq.username)
        val result = okHttpUtil.doGet(url, Pussy888Value.TransferResult::class.java)
        log.info("transfer result : $result")
        return transferReq.orderId
    }

    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): Any {
        val url = Pussy888Build.instance(GameConstant.PUSSY_API_ORDER_URL, path = "/ashx/GameLog.ashx")
                .set("pageIndex", "1")
                .set("pageSize", "1000")
                .set("userName", betOrderReq.username)
                .set("sDate", betOrderReq.startTime.format(dateTimeFormatter))
                .set("eDate", betOrderReq.endTime.format(dateTimeFormatter))
                .build(token = betOrderReq.token as Pussy888ClientToken, username = betOrderReq.username)

        val result = okHttpUtil.doGet(url, String::class.java)
        log.info("game log : $result")

        return result
    }

}
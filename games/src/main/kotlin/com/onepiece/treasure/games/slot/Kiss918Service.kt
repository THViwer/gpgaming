package com.onepiece.treasure.games.slot

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.Kiss918ClientToken
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.MapUtil
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
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
class Kiss918Service : PlatformService() {

    private val log = LoggerFactory.getLogger(Kiss918Service::class.java)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private fun sign(beforeParam: String?, username: String, time: Long, token: Kiss918ClientToken): String {
        val signStr = "${beforeParam?: ""}${token.autoCode}${username}${time}${token.key}".toLowerCase()
        return DigestUtils.md5Hex(signStr)
    }


    private fun startGetJson(method: String, beforeParam: String = "", username: String, data: List<String>, clientToken: Kiss918ClientToken): MapUtil {
        val time = System.currentTimeMillis()
        val sign = this.sign(beforeParam = beforeParam, username = username, time = time, token = clientToken)

        val param = data.joinToString(separator = "&")
        val url = "${gameConstant.getDomain(Platform.Kiss918)}?$param&sign=${sign}&authcode=${clientToken.autoCode}"

        val result = okHttpUtil.doGet(url = url, clz = Kiss918Value.Result::class.java)
        check(result.success) {  OnePieceExceptionCode.PLATFORM_DATA_FAIL }
        return result.mapUtil
    }

    private fun generatorUsername(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as Kiss918ClientToken
        val agentName = clientToken.agentName
        val data = listOf(
                "loginUser=${agentName}",
                "userName=${agentName}",
                "UserAreaId=1",
                "action=RandomUserName"

        )

        val mapUtil = this.startGetJson(method = "/ashx/account/account.ashx", beforeParam = agentName, username = agentName, clientToken = clientToken, data = data)
        return mapUtil.asString("account")
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as Kiss918ClientToken

        val agentName = clientToken.agentName
        val username = this.generatorUsername(registerReq)

        val data = listOf(
                "action=AddUser",
                "agent=${agentName}",
                "PassWd=${registerReq.password}",
                "userName=${username}",
                "Name=$username",
                "tel=1234124141241",
                "Memo=-",
                "UserType=1",
                "UserAreaId=1",
                "pwdtype=1"
        )
        this.startGetJson(method = "/ashx/account/account.ashx", username = username, clientToken = clientToken, data = data)
        return username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as Kiss918ClientToken

        val data = listOf(
                "action=getUserInfo",
                "userName=${balanceReq.username}"
        )

        val mapUtil = this.startGetJson(method = "/ashx/account/account.ashx", username = balanceReq.username, clientToken = clientToken, data = data)
        return mapUtil.asBigDecimal("MoneyNum")
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        val clientToken = transferReq.token as Kiss918ClientToken
        val data = listOf(
                "action=setServerScore",
                "orderid=${transferReq.orderId}",
                "scoreNum=${transferReq.amount}",
                "userName=${transferReq.username}",
                "ActionUse=system",
                "ActionIp=12.213.1.24"
        )

        this.startGetJson(method = "/ashx/account/setScore.ashx", username = transferReq.username, clientToken = clientToken, data = data)
        return transferReq.orderId
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        // TODO 暂时不实现
        error("")
    }

    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): Any {
        val clientToken = betOrderReq.token as Kiss918ClientToken
        val data = listOf(
                "pageIndex=1",
                "pageSize=1000",
                "userName=${betOrderReq.username}",
                "sDate=${betOrderReq.startTime.format(dateTimeFormatter)}",
                "eDate=${betOrderReq.endTime.format(dateTimeFormatter)}"
        )

        val mapUtils = this.startGetJson(method = "/ashx/GameLog.ashx", username = betOrderReq.username, clientToken = clientToken, data = data)
        return mapUtils.data
    }
}
package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.Kiss918ClientToken
import com.onepiece.gpgaming.beans.model.token.Pussy888ClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.MapUtil
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
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
class Pussy888Service(
        val queue:  FifoMap<String, Long> = FifoMap(100),
        val balanceQueue:  FifoMap<String, String> = FifoMap(100)
) : PlatformService() {

    private val log = LoggerFactory.getLogger(Pussy888Service::class.java)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private fun sign(beforeParam: String?, username: String, time: Long, token: Pussy888ClientToken): String {
        val signStr = "${beforeParam?: ""}${token.autoCode}${username}${time}${token.key}".toLowerCase()
        return DigestUtils.md5Hex(signStr).toUpperCase()
    }


    private fun startGetJson(url: String, beforeParam: String = "", username: String, data: List<String>, clientToken: Pussy888ClientToken): MapUtil {
        val time = System.currentTimeMillis()
        val sign = this.sign(beforeParam = beforeParam, username = username, time = time, token = clientToken)

        val param = data.joinToString(separator = "&")
        val requestUrl = "$url?sign=${sign}&time=$time&authcode=${clientToken.autoCode}&$param"

        val result = okHttpUtil.doGet(platform = Platform.Pussy888, url = requestUrl, clz = Kiss918Value.Result::class.java)
        check(result.success) {
            log.error("pussy888 network error: errorMsgId = ${result.success}, msg = ${result.msg}")
            OnePieceExceptionCode.PLATFORM_DATA_FAIL
        }
        return result.mapUtil
    }

    private fun generatorUsername(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as Pussy888ClientToken
        val agentName = clientToken.agentName
        val data = listOf(
                "loginUser=${agentName}",
                "userName=${agentName}",
                "UserAreaId=1",
                "action=RandomUserName"

        )

        val url = "${clientToken.apiPath}/ashx/account/account.ashx"
        val mapUtil = this.startGetJson(url = url, beforeParam = agentName, username = agentName, clientToken = clientToken, data = data)
        return mapUtil.asString("account")
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as Pussy888ClientToken

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
        val url = "${clientToken.apiPath}/ashx/account/account.ashx"
        this.startGetJson(url = url, username = username, clientToken = clientToken, data = data)
        return username
    }


    override fun updatePassword(updatePasswordReq: GameValue.UpdatePasswordReq) {
        val clientToken = updatePasswordReq.token as Pussy888ClientToken
        val agentName = clientToken.agentName

        val data = listOf(
                "action=editUser2",
                "agent=${agentName}",
                "PassWd=${updatePasswordReq.password}",
                "userName=${updatePasswordReq.username}",
                "Name=${updatePasswordReq.username}",
                "tel=1234124141241",
                "Memo=-",
                "UserType=1",
                "UserAreaId=1",
                "pwdtype=1"
        )

        val url = "${clientToken.apiPath}/ashx/account/account.ashx"
        this.startGetJson(url = url, username = updatePasswordReq.username, clientToken = clientToken, data = data)
    }
    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as Pussy888ClientToken

        log.info("查询余额. 请求ip: ${getRequestIp()}, " +
                "上次请求时间：${balanceQueue[balanceReq.username]}, " +
                "本次请求时间：${System.currentTimeMillis()}")
        val (balance, time) = (balanceQueue[balanceReq.username]?: "0_0").split("_")
        if (time != "0" && (System.currentTimeMillis() - time.toLong()) < 16000) {
            return balance.toBigDecimal()
        }

        val data = listOf(
                "action=getUserInfo",
                "userName=${balanceReq.username}"
        )

        val url = "${clientToken.apiPath}/ashx/account/account.ashx"
        val mapUtil = this.startGetJson(url = url , username = balanceReq.username, clientToken = clientToken, data = data)
        return mapUtil.asBigDecimal("MoneyNum")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {
        val clientToken = transferReq.token as Pussy888ClientToken

        log.info("请求ip: ${getRequestIp()}, " +
                "上次请求时间：${queue[transferReq.username]}, " +
                "本次请求时间：${System.currentTimeMillis()}, " +
                "时间相差:${System.currentTimeMillis() - (queue[transferReq.username]?: 0) / 1000}秒")
        val prev = queue[transferReq.username]?.let {
            (System.currentTimeMillis() - it) > 15000
        }?: true
        check(prev) { OnePieceExceptionCode.TRANSFER_TIME_FAST }

        val data = listOf(
                "action=setServerScore",
                "orderid=${transferReq.orderId}",
                "scoreNum=${transferReq.amount}",
                "userName=${transferReq.username}",
                "ActionUse=system",
                "ActionIp=12.213.1.24"
        )

        val url = "${clientToken.apiPath}/ashx/account/setScore.ashx"
        val mapUtil = this.startGetJson(url = url, username = transferReq.username, clientToken = clientToken, data = data)
        val balance = mapUtil.asBigDecimal("money")

        queue[transferReq.username] = System.currentTimeMillis()

        return GameValue.TransferResp.successful(balance = balance)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {

        val clientToken = checkTransferReq.token as Pussy888ClientToken

        val now = LocalDateTime.now()
        val data = listOf(
                "pageIndex=1",
                "userName=${checkTransferReq.username}",
                "sDate=${now.minusMinutes(2).format(dateTimeFormatter)}",
                "eDate=${now.format(dateTimeFormatter)}",
                "authcode=${clientToken.autoCode}"
        )

        val url = "${clientToken.apiOrderPath}/ashx/UserscoreLog.ashx"
        val mapUtil = this.startGetJson(url = url, username = checkTransferReq.username, clientToken = clientToken, data = data)

        val flag = mapUtil.asList("results").firstOrNull()?.asBigDecimal("ScoreNum")?.setScale(2, 2) == checkTransferReq.amount.setScale(2, 2)
        return GameValue.TransferResp.of(successful = flag)
    }

    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): List<BetOrderValue.BetOrderCo> {
        val clientToken = betOrderReq.token as Pussy888ClientToken

        val endTime = LocalDateTime.now()
        val startTime = LocalDate.now().atStartOfDay()
        val data = listOf(
                "pageIndex=1",
                "pageSize=1000",
                "userName=${betOrderReq.username}",
//                "sDate=${betOrderReq.startTime.format(dateTimeFormatter)}",
//                "eDate=${betOrderReq.endTime.format(dateTimeFormatter)}"
                "sDate=${startTime.format(dateTimeFormatter)}",
                "eDate=${endTime.format(dateTimeFormatter)}"
        )

        val url = "${clientToken.apiOrderPath}/ashx/GameLog.ashx"
        val mapUtils = this.startGetJson(url = url, username = betOrderReq.username, clientToken = clientToken, data = data)

        val clientId = -1
        val memberId = -1
        return mapUtils.asList("results").map { bet ->
            val orderId = bet.asString("uuid")
            val betAmount = bet.asBigDecimal("bet")
            val winAmount = bet.asBigDecimal("Win")
            val betTime = bet.asLocalDateTime("CreateTime", dateTimeFormatter)

            val originData = objectMapper.writeValueAsString(bet.data)

            BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.Kiss918, orderId = orderId, betAmount = betAmount,
                    winAmount = winAmount, betTime = betTime, settleTime = betTime, originData = originData)
        }
    }
}
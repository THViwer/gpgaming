package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.model.token.Kiss918ClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.PlatformUsernameUtil
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
class Kiss918Service : PlatformService() {

    private val log = LoggerFactory.getLogger(Kiss918Service::class.java)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private fun sign(beforeParam: String?, username: String, time: Long, token: Kiss918ClientToken): String {
        val signStr = "${beforeParam?: ""}${token.autoCode}${username}${time}${token.key}".toLowerCase()
        return DigestUtils.md5Hex(signStr).toUpperCase()
    }


    private fun startGetJson(url: String, beforeParam: String = "", username: String, data: List<String>, clientToken: Kiss918ClientToken): MapUtil {
        val time = System.currentTimeMillis()
        val sign = this.sign(beforeParam = beforeParam, username = username, time = time, token = clientToken)

        val param = data.joinToString(separator = "&")
        val requestUrl = "$url?$param&sign=${sign}&time=$time&authcode=${clientToken.autoCode}"

        val result = okHttpUtil.doGet(url = requestUrl, clz = Kiss918Value.Result::class.java)
        check(result.success) {
            log.error("kiss918 接口error: $result")
            OnePieceExceptionCode.PLATFORM_DATA_FAIL
        }
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

        val url = "${gameConstant.getDomain(Platform.Kiss918)}/ashx/account/account.ashx"
        val mapUtil = this.startGetJson(url = url, beforeParam = agentName, username = agentName, clientToken = clientToken, data = data)
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
        val url = "${gameConstant.getDomain(Platform.Kiss918)}/ashx/account/account.ashx"
        this.startGetJson(url = url, username = username, clientToken = clientToken, data = data)
        return username
    }

    override fun updatePassword(updatePasswordReq: GameValue.UpdatePasswordReq) {
        val clientToken = updatePasswordReq.token as Kiss918ClientToken
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
        val url = "${gameConstant.getDomain(Platform.Kiss918)}/ashx/account/account.ashx"
        this.startGetJson(url = url, username = updatePasswordReq.username, clientToken = clientToken, data = data)
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as Kiss918ClientToken

        val data = listOf(
                "action=getUserInfo",
                "userName=${balanceReq.username}"
        )

        val url = "${gameConstant.getDomain(Platform.Kiss918)}/ashx/account/account.ashx"
        val mapUtil = this.startGetJson(url = url, username = balanceReq.username, clientToken = clientToken, data = data)
        return mapUtil.asBigDecimal("MoneyNum")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {
        val clientToken = transferReq.token as Kiss918ClientToken
        val data = listOf(
                "action=setServerScore",
                "orderid=${transferReq.orderId}",
                "scoreNum=${transferReq.amount}",
                "userName=${transferReq.username}",
                "ActionUse=system",
                "ActionIp=12.213.1.24"
        )

        val url = "${gameConstant.getDomain(Platform.Kiss918)}/ashx/account/setScore.ashx"
        val mapUtil = this.startGetJson(url = url, username = transferReq.username, clientToken = clientToken, data = data)
        val balance = mapUtil.asBigDecimal("money")
        return GameValue.TransferResp.successful(balance)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        // TODO 暂时不实现
        return GameValue.TransferResp.failed()
    }

    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): List<BetOrderValue.BetOrderCo> {
        val clientToken = betOrderReq.token as Kiss918ClientToken

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

        val url = "${gameConstant.getOrderApiUrl(Platform.Kiss918)}/ashx/GameLog.ashx"
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


//
//    fun queryBetReport(token: ClientToken, username: String, startDate: LocalDate): BigDecimal {
//        val clientToken = token as Kiss918ClientToken
//
//        val endDate = LocalDate.now()
//        val data = listOf(
//                "userName=$username",
//                "sDate=${startDate}",
//                "eDate=${endDate}"
//        )
//
//        val url = "${gameConstant.getDomain(Platform.Kiss918)}/ashx/AgentMoneyLog.ashx"
//        val mapUtil = this.startGetJson(url = url, username = username, clientToken = clientToken, data = data)
//
//
//
//        return transferReq.orderId
//
//
//
//    }



}
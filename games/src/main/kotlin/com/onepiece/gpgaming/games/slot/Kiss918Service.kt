package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.U9RequestStatus
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.Kiss918ClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
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
class Kiss918Service(
        val queue: FifoMap<String, Long> = FifoMap(100),
        val balanceQueue: FifoMap<String, String> = FifoMap(100)
) : PlatformService() {

    private val log = LoggerFactory.getLogger(Kiss918Service::class.java)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private fun sign(beforeParam: String?, username: String, time: Long, token: Kiss918ClientToken): String {
        val signStr = "${beforeParam ?: ""}${token.autoCode}${username}${time}${token.key}".toLowerCase()
        return DigestUtils.md5Hex(signStr).toUpperCase()
    }


    private fun doGet(url: String, beforeParam: String = "", username: String, data: List<String>, clientToken: Kiss918ClientToken): OKResponse {
        val time = System.currentTimeMillis()
        val sign = this.sign(beforeParam = beforeParam, username = username, time = time, token = clientToken)

        val param = "${data.joinToString(separator = "&")}&sign=${sign}&time=$time&authcode=${clientToken.autoCode}".replace(" ", "%20")
        val okParam = OKParam.ofGet(url = url, param = param)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)

        if (!okResponse.ok) return okResponse

        val status = try {
            when (okResponse.asBoolean("success")) {
                true -> U9RequestStatus.OK
                false -> U9RequestStatus.Fail
            }
        } catch (e: Exception) {
            U9RequestStatus.Fail
        }
        return okResponse.copy(status = status)
    }

    private fun generatorUsername(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val clientToken = registerReq.token as Kiss918ClientToken
        val agentName = clientToken.agentName
        val data = listOf(
                "loginUser=${agentName}",
                "userName=${agentName}",
                "UserAreaId=1",
                "action=RandomUserName"
        )

        val url = "${clientToken.apiPath}/ashx/account/account.ashx"
        val okResponse = this.doGet(url = url, beforeParam = agentName, username = agentName, clientToken = clientToken, data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asString("account")
        }
    }


    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val clientToken = registerReq.token as Kiss918ClientToken

        val agentName = clientToken.agentName
        val gResponse = this.generatorUsername(registerReq)
        if (!gResponse.okResponse.ok) return gResponse

        val username = gResponse.data!!


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
        val okResponse = this.doGet(url = url, username = username, clientToken = clientToken, data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            username
        }
    }

    override fun updatePassword(updatePasswordReq: GameValue.UpdatePasswordReq): GameResponse<Unit> {
        val clientToken = updatePasswordReq.token as Kiss918ClientToken
        val agentName = clientToken.agentName

        val data = listOf(
                "action=editUser2",
                "agent=${agentName}",
                "PassWd=${updatePasswordReq.password}",
                "userName=${updatePasswordReq.username}",
                "Flag=1",
                "Name=${updatePasswordReq.username}",
                "tel=1234124141241",
                "Memo=-",
                "UserType=1",
                "UserAreaId=1",
                "pwdtype=1"
        )
        val url = "${clientToken.apiPath}/ashx/account/account.ashx"
        val okResponse = this.doGet(url = url, username = updatePasswordReq.username, clientToken = clientToken, data = data)
        return this.bindGameResponse(okResponse) {}
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as Kiss918ClientToken

        log.info("查询余额. 请求ip: ${getRequestIp()}, " +
                "上次请求时间：${balanceQueue[balanceReq.username]}, " +
                "本次请求时间：${System.currentTimeMillis()}")
        val (balance, time) = (balanceQueue[balanceReq.username] ?: "0_0").split("_")
        if (time != "0" && (System.currentTimeMillis() - time.toLong()) < 16000) {
            return GameResponse.of(data = balance.toBigDecimal())
        }

        val data = listOf(
                "action=getUserInfo",
                "userName=${balanceReq.username}"
        )

        val url = "${clientToken.apiPath}/ashx/account/account.ashx"
        val okResponse = this.doGet(url = url, username = balanceReq.username, clientToken = clientToken, data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            val amount = it.asBigDecimal("MoneyNum")
            balanceQueue[balanceReq.username] = "${amount}_${System.currentTimeMillis()}"
            amount
        }

    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = transferReq.token as Kiss918ClientToken

        log.info("转账.请求ip: ${getRequestIp()}, " +
                "上次请求时间：${queue[transferReq.username]}, " +
                "本次请求时间：${System.currentTimeMillis()}, " +
                "时间相差:${System.currentTimeMillis() - (queue[transferReq.username] ?: 0) / 1000}秒")

        val prev = queue[transferReq.username]?.let {
            (System.currentTimeMillis() - it) > 15000
        } ?: true
        check(prev) { OnePieceExceptionCode.TRANSFER_TIME_FAST }

        val data = listOf(
                "action=setServerScore",
                "orderid=${transferReq.orderId}",
                "scoreNum=${transferReq.amount}",
                "userName=${transferReq.username}",
                "ActionUse=system",
                "ActionIp=${getRequestIp()}"
        )

        return try {
            val url = "${clientToken.apiPath}/ashx/account/setScore.ashx"
            val okResponse = this.doGet(url = url, username = transferReq.username, clientToken = clientToken, data = data)

            this.bindGameResponse(okResponse = okResponse) {
                val balance = it.asBigDecimal("money")

                queue[transferReq.username] = System.currentTimeMillis()

                GameValue.TransferResp.successful(balance)
            }

        } catch (e: Exception) {
            if (e.message?.contains("fail,money not enough,") != null) {
                GameResponse.of(GameValue.TransferResp.failed())
            } else {
                throw e
            }
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {

        val clientToken = checkTransferReq.token as Kiss918ClientToken

        val now = LocalDateTime.now()
        val data = listOf(
                "pageIndex=1",
                "userName=${checkTransferReq.username}",
                "sDate=${now.minusMinutes(2).format(dateTimeFormatter)}",
                "eDate=${now.format(dateTimeFormatter)}",
                "authcode=${clientToken.autoCode}"
        )

        val url = "${clientToken.apiOrderPath}/ashx/UserscoreLog.ashx"
        val okResponse = this.doGet(url = url, username = checkTransferReq.username, clientToken = clientToken, data = data)

        return this.bindGameResponse(okResponse = okResponse) {
            val flag = it.asList("results").firstOrNull()?.asBigDecimal("ScoreNum")?.setScale(2, 2) == checkTransferReq.amount.setScale(2, 2)
            GameValue.TransferResp.of(successful = flag)
        }

    }

    override fun queryReport(reportQueryReq: GameValue.ReportQueryReq): GameResponse<List<GameValue.PlatformReportData>> {

        val clientToken = reportQueryReq.token as Kiss918ClientToken

        val data = listOf(
                "sDate=${reportQueryReq.startDate}",
                "eDate=${reportQueryReq.startDate.plusDays(1)}",
                "userName=${clientToken.agentName}",
                "Type=ServerTotalReport"
        )

        val url = "${clientToken.apiOrderPath}/ashx/AgentTotalReport.ashx"
        val okResponse = this.doGet(url = url, username = clientToken.agentName, clientToken = clientToken, data = data)
        return this.bindGameResponse(okResponse) {
            it.asList("results").map {
                val username = it.asString("Account")
                val bet = BigDecimal.valueOf(-1)
                val win = it.asBigDecimal("win")
                val originData = objectMapper.writeValueAsString(it.data)

                GameValue.PlatformReportData(username = username, platform = Platform.Kiss918, bet = bet, win = win,
                        originData = originData)

            }
        }
    }


    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {
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

        val url = "${clientToken.apiOrderPath}/ashx/GameLog.ashx"
        val okResponse = this.doGet(url = url, username = betOrderReq.username, clientToken = clientToken, data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            val clientId = -1
            val memberId = -1
            it.asList("results").map { bet ->
                val orderId = bet.asString("uuid")
//            val betAmount = bet.asBigDecimal("bet")
                val betAmount = BigDecimal.ZERO
                val winAmount = bet.asBigDecimal("Win").negate()
                val betTime = bet.asLocalDateTime("CreateTime", dateTimeFormatter)

                val originData = objectMapper.writeValueAsString(bet.data)

                BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.Kiss918, orderId = orderId, betAmount = betAmount,
                        winAmount = winAmount, betTime = betTime, settleTime = betTime, originData = originData, validAmount = betAmount)
            }
        }


    }

}


class FifoMap<K, V>(
        private val maximumSize: Int
) : LinkedHashMap<K, V>() {

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size > maximumSize
    }
}

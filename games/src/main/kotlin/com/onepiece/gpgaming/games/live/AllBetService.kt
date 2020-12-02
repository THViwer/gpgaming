package com.onepiece.gpgaming.games.live

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.U9RequestStatus
import com.onepiece.gpgaming.beans.model.token.AllBetClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.DesUtil
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class AllBetService : PlatformService() {

    private val log = LoggerFactory.getLogger(AllBetService::class.java)
    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")


    fun doGet(method: String, urlParam: String, allBetClientToken: AllBetClientToken): OKResponse {

        val desData = DesUtil.encrypt(urlParam, allBetClientToken.desKey, null)
        val md5Data = Base64.encodeBase64String(DigestUtils.md5("$desData${allBetClientToken.md5Key}"))
        val param = "propertyId=${allBetClientToken.propertyId}&data=${URLEncoder.encode(desData, "UTF-8")}&sign=${URLEncoder.encode(md5Data, "UTF-8")}&${urlParam}"

//        val result = okHttpUtil.doGet(platform = Platform.AllBet, url = "http://193.200.134.198:1001${method}?$param", clz = AllBetValue.Result::class.java)
        val url = "${allBetClientToken.apiPath}${method}"
        val okParam = OKParam.ofGet(url = url, param = param)
        val okResponse = u9HttpRequest.startRequest(okParam)

        if (!okResponse.ok) return okResponse

        val status = try {
            when (okResponse.asString("error_code")) {
                "OK" -> U9RequestStatus.OK
                "SYSTEM_MATAINING" -> U9RequestStatus.Maintain
                else -> U9RequestStatus.Fail
            }
        } catch (e: Exception) {
            U9RequestStatus.Fail
        }
        return okResponse.copy(status = status)
    }

    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val allBetClientToken = registerReq.token as AllBetClientToken

        // 查询盘口
//        val (orHallRebate, vipHandicapNames) = this.queryHandicap(allBetClientToken)

        val data = listOf(
                "agent=${allBetClientToken.agentName}",
                "client=${registerReq.username}",
                "password=${registerReq.password}",
                "orHandicapNames=${allBetClientToken.orHandicapNames}",
                "vipHandicapNames=${allBetClientToken.vipHandicapNames}",
                "orHallRebate=0",
//                "vipHandicaps=12",
//                "orHandicaps=1",
//                "orHallRebate=0.5",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = data.joinToString(separator = "&")

        val okResponse = this.doGet(method = "/check_or_create", urlParam = urlParam, allBetClientToken = allBetClientToken)
        return this.bindGameResponse(okResponse = okResponse) { mapUtil ->
            mapUtil.asString("client")
        }
    }

    override fun updatePassword(updatePasswordReq: GameValue.UpdatePasswordReq): GameResponse<Unit> {

        val allBetClientToken = updatePasswordReq.token as AllBetClientToken

        val data = listOf(
                "client=${updatePasswordReq.username}",
                "newPassword=${updatePasswordReq.password}",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = data.joinToString(separator = "&")
        val okResponse = this.doGet(method = "/setup_client_password", urlParam = urlParam, allBetClientToken = allBetClientToken)
        return this.bindGameResponse(okResponse = okResponse) {}
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val allBetClientToken = balanceReq.token as AllBetClientToken

        val data = listOf(
                "client=${balanceReq.username}",
                "password=${balanceReq.password}",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = data.joinToString(separator = "&")
        val okResponse = this.doGet(method = "/get_balance", urlParam = urlParam, allBetClientToken = allBetClientToken)

        return this.bindGameResponse(okResponse) { mapUtil ->
            mapUtil.asBigDecimal("balance")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
        val allBetClientToken = transferReq.token as AllBetClientToken

        val operFlag = if (transferReq.amount.toDouble() > 0) 1 else 0

        val allBetOrderId = "${allBetClientToken.propertyId}${transferReq.orderId}"
        val data = listOf(
                "agent=${allBetClientToken.agentName}",
                "sn=${allBetOrderId}",
                "client=${transferReq.username}",
                "operFlag=${operFlag}",
                "credit=${transferReq.amount.abs()}",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = data.joinToString("&")
        val response = this.doGet(method = "/agent_client_transfer", urlParam = urlParam, allBetClientToken = allBetClientToken)
        return this.bindGameResponse(okResponse = response) {
            GameValue.TransferResp.successful(platformOrderId = allBetOrderId)
        }
    }


    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        val allBetClientToken = checkTransferReq.token as AllBetClientToken

        // query_transfer_state
        val allBetOrderId = "${allBetClientToken.propertyId}${checkTransferReq.orderId}"
        val data = listOf(
                "sn=${allBetOrderId}",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = data.joinToString(separator = "&")
        val response = this.doGet(method = "/query_transfer_state", urlParam = urlParam, allBetClientToken = allBetClientToken)
        return this.bindGameResponse(okResponse = response) { mapUtil ->
            // 0 创建状态 1 成功 2 失败
            val successful = mapUtil.asInt("transferState") == 1
            GameValue.TransferResp.of(successful)
        }
    }

    /**
     * 中文简体	zh_CN
     * 中文繁体	zh_TW
     * 英文	en
     * 韩语	ko
     * 泰文	th
     * 马来文	ms	H5不支持
     * 越南文	vi	H5不支持
     * 西班牙语	es-es	H5不支持
     * 日语	ja
     * 俄罗斯语	ru	H5不支持
     * 印度尼西亚语	id	H5不支持
     */
    override fun start(startReq: GameValue.StartReq): GameResponse<String> {
        val language = when (startReq.language) {
            Language.TH -> "th"
            Language.VI -> "vi"
            Language.CN -> "zh_CN"
            Language.EN -> "en"
            Language.MY -> "ms"
            Language.ID -> "id"
            else -> "en"
        }

        val allBetClientToken = startReq.token as AllBetClientToken
        val data = listOf(
                "client=${startReq.username}",
                "password=${startReq.password}",
                "language=$language",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = data.joinToString(separator = "&")
        val okResponse = this.doGet(method = "/forward_game", urlParam = urlParam, allBetClientToken = allBetClientToken)
        return this.bindGameResponse(okResponse = okResponse) { mapUtil ->
            mapUtil.asString("gameLoginUrl")
        }
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {

        val allBetClientToken = pullBetOrderReq.token as AllBetClientToken
        val param = listOf(
                "client=${allBetClientToken.agentName}",
//                "egameType=af",
                "startTime=${pullBetOrderReq.startTime.format(dateTimeFormat)}",
                "endTime=${pullBetOrderReq.endTime.format(dateTimeFormat)}",
//                "pageIndex=1",
//                "pageSize=1000",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = param.joinToString(separator = "&")
        val okResponse = this.doGet(method = "/betlog_pieceof_histories_in30days", urlParam = urlParam, allBetClientToken = allBetClientToken)

        return this.bindGameResponse(okResponse = okResponse) { mapUtil ->
            val page = mapUtil.asList("histories")
            page.map { bet ->
                val orderId = bet.asString("betNum")
                val username = bet.asString("client")
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.AllBet, platformUsername = username)
                val betAmount = bet.asBigDecimal("betAmount")
                val validAmount = bet.asBigDecimal("validAmount")
                val winOrLoss = bet.asBigDecimal("winOrLoss")
                val payout = betAmount.plus(winOrLoss)
                val betTime = bet.asLocalDateTime("gameRoundStartTime", dateTimeFormat)
                val settleTime = bet.asLocalDateTime("gameRoundEndTime", dateTimeFormat)

                val originData = objectMapper.writeValueAsString(bet.data)
                BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, betAmount = betAmount, payout = payout, betTime = betTime,
                        settleTime = settleTime, platform = Platform.AllBet, originData = originData, validAmount = validAmount)
            }
        }

    }

}
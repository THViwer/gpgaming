package com.onepiece.gpgaming.games.sport

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.token.CMDClientToken
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Exception
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class CMDService : PlatformService() {

    companion object {
        const val CMD_HASH = "afawefweaef"
    }

    private val log = LoggerFactory.getLogger(CMDService::class.java)

    fun doGet(clientToken: CMDClientToken, data: List<String>): OKResponse {
        val param = data.joinToString(separator = "&")
        val url = "${clientToken.apiPath}/SportsApi.aspx"

        val okParam = OKParam.ofGet(url = url, param = param)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)
        if (!okResponse.ok) return okResponse

        val ok = try {
            val code = okResponse.asInt("Code")
            code == 0 || code == -102
        } catch (e: Exception) {
            false
        }

        return okResponse.copy(ok = ok)
    }


    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val cmdClientToken = registerReq.token as CMDClientToken

        val data = listOf(
                "Method=createmember",
                "PartnerKey=${cmdClientToken.partnerKey}",
                "UserName=${registerReq.username}",
                "Currency=${cmdClientToken.currency}"
        )

        val okResponse = this.doGet(clientToken = cmdClientToken, data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            registerReq.username
        }
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val cmdClientToken = balanceReq.token as CMDClientToken

        val data = listOf(
                "Method=getbalance",
                "PartnerKey=${cmdClientToken.partnerKey}",
                "UserName=${balanceReq.username}"
        )

        val okResponse = this.doGet(clientToken = cmdClientToken, data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asList("Data").first().asBigDecimal("BetAmount")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
        val cmdClientToken = transferReq.token as CMDClientToken

        val paymentType = if (transferReq.amount.toDouble() > 0) 1 else 0
        val data = listOf(
                "Method=balancetransfer",
                "PartnerKey=${cmdClientToken.partnerKey}",
                "UserName=${transferReq.username}",
                "PaymentType=$paymentType",
                "Money=${transferReq.amount.abs()}",
                "TicketNo=${transferReq.orderId}"
        )
        val okResponse = this.doGet(clientToken = cmdClientToken, data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            val platformOrderId = it.asMap("Data").asString("PaymentId")
            val balance = it.asMap("Data").asBigDecimal("BetAmount")
            GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
        }

    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        val cmdClientToken = checkTransferReq.token as CMDClientToken

        val data = listOf(
                "Method=checkfundtransferstatus",
                "PartnerKey=${cmdClientToken.partnerKey}",
                "UserName=${checkTransferReq.username}",
                "TicketNo=${checkTransferReq.orderId}"
        )
        val okResponse = this.doGet(clientToken = cmdClientToken, data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            val successful = it.asList("Data").size == 1
            GameValue.TransferResp.of(successful)
        }
    }

    override fun startDemo(token: ClientToken, language: Language, launch: LaunchMethod): GameResponse<String> {

        val clientToken = token as CMDClientToken
        val lang = when (language) {
            Language.CN -> "zh-CN"
            Language.TH -> "th-TH"
            Language.VI -> "vi-VN"
            Language.ID -> "id-ID"
            Language.EN -> "en-US"
            else -> "en-US"
        }

        // 模板：aliceblue、blue、bluegray、darker、gray、green

        val path = when (launch) {
            LaunchMethod.Wap -> "${clientToken.mobileGamePath}/?lang=$lang&templatename=aliceblue"
            else -> "${clientToken.gamePath}/?lang=$lang&templatename=aliceblue"
        }
        return GameResponse.of(data = path)
    }

    override fun start(startReq: GameValue.StartReq): GameResponse<String> {

        val clientToken = startReq.token as CMDClientToken

        val lang = when (startReq.language) {
            Language.CN -> "zh-CN"
            Language.TH -> "th-TH"
            Language.VI -> "vi-VN"
            Language.ID -> "id-ID"
            Language.EN -> "en-US"
            else -> "en-US"
        }

        val token = DigestUtils.md5Hex("${startReq.username}:$CMD_HASH")


        val path = if (startReq.launch == LaunchMethod.Web) clientToken.gamePath else clientToken.mobileGamePath
        // view: v1 = 传统风格 v2 = 亚洲风格 v3 = 电子竞技风格
        val url = "$path/auth.aspx?lang=$lang&user=${startReq.username}&token=$token&currency=${clientToken.currency}&templatename=aliceblue&view=v1"
        return GameResponse.of(data = url)
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {
        val cmdClientToken = pullBetOrderReq.token as CMDClientToken

        return this.pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.CMD) { startId ->

            val data = listOf(
                    "Method=betrecord",
                    "PartnerKey=${cmdClientToken.partnerKey}",
                    "Version=${startId}"
            )
            val okResponse = this.doGet(clientToken = cmdClientToken, data = data)
            var nextId: String = startId

            val gameResponse = this.bindGameResponse(okResponse = okResponse) {
                it.asList("Data").filter { it.asString("WinLoseStatus") != "P" }.map { bet ->

                    val orderId = bet.asString("Id")
                    val username = bet.asString("SourceName")
                    val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.CMD, platformUsername = username)
                    val betAmount = bet.asBigDecimal("BetAmount")
                    val winAmount = bet.asBigDecimal("WinAmount")
                    val betTime = LocalDateTime.ofInstant(Instant.ofEpochMilli((bet.asLong("TransDate") - 621355968000000000) / 10000), ZoneId.of("Asia/Shanghai")).minusHours(8)
                    val settleTime = LocalDateTime.ofInstant(Instant.ofEpochMilli((bet.asLong("StateUpdateTs") - 621355968000000000) / 10000), ZoneId.of("Asia/Shanghai")).minusHours(8)

                    val originData = objectMapper.writeValueAsString(bet.data)
                    if (nextId < orderId) nextId = orderId

                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, platform = Platform.CMD, betAmount = betAmount, winAmount = winAmount,
                            betTime = betTime, settleTime = settleTime, originData = originData, validAmount = betAmount)
                }
            }

            BetNextIdData(nextId = nextId, gameResponse = gameResponse)
        }

    }
}
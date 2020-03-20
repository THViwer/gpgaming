package com.onepiece.gpgaming.games.sport

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.CMDClientToken
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.MapUtil
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
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

    fun startGetJson(clientToken: CMDClientToken, data: List<String>): MapUtil {
        val param = data.joinToString(separator = "&")
        val url = "${clientToken.apiPath}/SportsApi.aspx?$param"

        val result = okHttpUtil.doGet(platform = Platform.CMD, url = url, clz = CMDValue.Result::class.java)
        check(result.code == 0 || result.code == -102) {
            log.error("cmd network error: code = ${result.code}")
            OnePieceExceptionCode.PLATFORM_DATA_FAIL
        }

        return result.mapUtil
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val cmdClientToken = registerReq.token as CMDClientToken

        val data = listOf(
                "Method=createmember",
                "PartnerKey=${cmdClientToken.partnerKey}",
                "UserName=${registerReq.username}",
                "Currency=${cmdClientToken.currency}"
        )

        this.startGetJson(clientToken = cmdClientToken, data = data)
        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val cmdClientToken = balanceReq.token as CMDClientToken

        val data = listOf(
                "Method=getbalance",
                "PartnerKey=${cmdClientToken.partnerKey}",
                "UserName=${balanceReq.username}"
        )

        val mapUtil = this.startGetJson(clientToken = cmdClientToken, data = data)
        return mapUtil.asList("Data").first().asBigDecimal("BetAmount")

    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {
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
        val mapUtil = this.startGetJson(clientToken = cmdClientToken, data = data)
        val platformOrderId = mapUtil.asMap("Data").asString("PaymentId")
        val balance = mapUtil.asMap("Data").asBigDecimal("BetAmount")
        return GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        val cmdClientToken = checkTransferReq.token as CMDClientToken

        val data = listOf(
                "Method=checkfundtransferstatus",
                "PartnerKey=${cmdClientToken.partnerKey}",
                "UserName=${checkTransferReq.username}",
                "TicketNo=${checkTransferReq.orderId}"
        )
        val mapUtil = this.startGetJson(clientToken = cmdClientToken, data = data)
        val successful = mapUtil.asList("Data").size == 1
        return GameValue.TransferResp.of(successful)
    }

    override fun startDemo(token: ClientToken, language: Language, launch: LaunchMethod): String {

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

        return when (launch) {
            LaunchMethod.Wap -> "https://gp8mobile.1win888.net/?lang=$lang&templatename=aliceblue"
            else -> "${clientToken.gamePath}/?lang=$lang&templatename=aliceblue"
        }
    }

    override fun start(startReq: GameValue.StartReq): String {

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
        return "$path/auth.aspx?lang=$lang&user=${startReq.username}&token=$token&currency=${clientToken.currency}&templatename=aliceblue&view=v1"
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val cmdClientToken = pullBetOrderReq.token as CMDClientToken

        return this.pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.CMD) { startId ->

            val data = listOf(
                    "Method=betrecord",
                    "PartnerKey=${cmdClientToken.partnerKey}",
                    "Version=${startId}"
            )
            val mapUtil = this.startGetJson(clientToken = cmdClientToken, data = data)

            var nextId: String = startId
            val orders = mapUtil.asList("Data").filter { it.asString("WinLoseStatus") != "P" }.map { bet ->

                val orderId = bet.asString("Id")
                val username = bet.asString("SourceName")
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.CMD, platformUsername = username)
                val betAmount = bet.asBigDecimal("BetAmount")
                val winAmount = bet.asBigDecimal("WinAmount")
                val betTime = LocalDateTime.ofInstant(Instant.ofEpochMilli((bet.asLong("TransDate")-621355968000000000)/10000), ZoneId.of("Asia/Shanghai")).minusHours(8)
                val settleTime = LocalDateTime.ofInstant(Instant.ofEpochMilli((bet.asLong("StateUpdateTs")-621355968000000000)/10000), ZoneId.of("Asia/Shanghai")).minusHours(8)

                val originData = objectMapper.writeValueAsString(bet.data)
                if (nextId < orderId) nextId = orderId

                BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, platform = Platform.CMD, betAmount = betAmount, winAmount = winAmount,
                        betTime = betTime, settleTime = settleTime, originData = originData)
            }

            nextId to orders
        }

    }
}
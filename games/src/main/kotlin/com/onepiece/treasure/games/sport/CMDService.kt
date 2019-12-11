package com.onepiece.treasure.games.sport

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.CMDClientToken
import com.onepiece.treasure.beans.model.token.ClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.MapUtil
import org.apache.commons.codec.digest.DigestUtils
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

    fun startGetJson(data: List<String>): MapUtil {
        val param = data.joinToString(separator = "&")
        val url = "${gameConstant.getDomain(Platform.CMD)}/SportsApi.aspx?$param"

        val result = okHttpUtil.doGet(url = url, clz = CMDValue.Result::class.java)
        check(result.code == 0 || result.code == -102) { OnePieceExceptionCode.PLATFORM_DATA_FAIL }

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

        this.startGetJson(data)
        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val cmdClientToken = balanceReq.token as CMDClientToken

        val data = listOf(
                "Method=getbalance",
                "PartnerKey=${cmdClientToken.partnerKey}",
                "UserName=${balanceReq.username}"
        )

        val mapUtil = this.startGetJson(data)
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
        val mapUtil = this.startGetJson(data)
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
        val mapUtil = this.startGetJson(data)
        val successful = mapUtil.asList("Data").size == 1
        return GameValue.TransferResp.of(successful)
    }

    override fun startDemo(token: ClientToken, language: Language, launch: LaunchMethod): String {

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
            LaunchMethod.Wap -> "http://gp8mobile.1win888.net/?lang=$lang&templatename=aliceblue"
            else -> "http://gp8.1win888.net/?lang=$lang&templatename=aliceblue"
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

        val domain = when (startReq.launch) {
            LaunchMethod.Wap -> "http://gp8mobile.1win888.net"
            else -> "http://gp8.1win888.net"
        }
        // view: v1 = 传统风格 v2 = 亚洲风格 v3 = 电子竞技风格
        return "$domain/auth.aspx?lang=$lang&user=${startReq.username}&token=$token&currency=${clientToken.currency}&templatename=aliceblue&view=v2"
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val cmdClientToken = pullBetOrderReq.token as CMDClientToken

        return this.pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.CMD) { startId ->

            val data = listOf(
                    "Method=betrecord",
                    "PartnerKey=${cmdClientToken.partnerKey}",
                    "Version=${startId}"
            )
            val mapUtil = this.startGetJson(data)

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
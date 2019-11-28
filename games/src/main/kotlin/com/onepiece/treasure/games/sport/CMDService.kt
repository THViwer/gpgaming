package com.onepiece.treasure.games.sport

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.CMDClientToken
import com.onepiece.treasure.beans.model.token.ClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.BetOrderUtil
import com.onepiece.treasure.games.bet.MapUtil
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class CMDService : PlatformService() {

    companion object {
        const val CMD_HASH = "afawefweaef"
    }

    fun startGetJson(data: List<String>): MapUtil {
        val param = data.joinToString(separator = "&")
        val url = "${gameConstant.getDomain(Platform.CMD)}/SportsApi.aspx?$param"

        val result = okHttpUtil.doGet(url = url, clz = CMDValue.Result::class.java)
        check(result.code == 0) { OnePieceExceptionCode.PLATFORM_DATA_FAIL }

        return result.mapUtil
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val cmdClientToken = registerReq.token as CMDClientToken

        val data = listOf(
                "Method=createmember",
                "PartnerKey=${cmdClientToken.partnerKey}",
                "UserName=${registerReq.username}",
                "Currency=MYR"
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

    override fun transfer(transferReq: GameValue.TransferReq): String {
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
        return mapUtil.asMap("Data").asString("PaymentId")
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val cmdClientToken = checkTransferReq.token as CMDClientToken

        val data = listOf(
                "Method=checkfundtransferstatus",
                "PartnerKey=${cmdClientToken.partnerKey}",
                "serName=${checkTransferReq.orderId}"
        )
        val mapUtil = this.startGetJson(data)
        return mapUtil.asMap("Data").data["Status"] == "1"
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
        return "http://gp8mobile.1win888.net?lang=$lang&templatename=aliceblue"
    }

    override fun start(startReq: GameValue.StartReq): String {

        val lang = when (startReq.language) {
            Language.CN -> "zh-CN"
            Language.TH -> "th-TH"
            Language.VI -> "vi-VN"
            Language.ID -> "id-ID"
            Language.EN -> "en-US"
            else -> "en-US"
        }

        val token = DigestUtils.md5Hex("${startReq.username}:$CMD_HASH")

        // view: v1 = 传统风格 v2 = 亚洲风格 v3 = 电子竞技风格
        return "http://gp8.1win888.net/auth.aspx?lang=$lang&user=${startReq.username}&token=$token&currency=MYR&templatename=aliceblue&view=v2"
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

            val orders = mapUtil.asList("Data").filter { it.asString("WinLoseStatus") != "P" }.map { bet ->
                BetOrderUtil.instance(platform = Platform.CMD, mapUtil = bet)
                        .setOrderId("Id")
                        .setUsername("SourceName")
                        .setBetAmount("BetAmount")
                        .setWinAmount("WinAmount")
                        .setBetTimeByLong("StateUpdateTs")
                        .setSettleTimeByLong("StateUpdateTs")
                        .build(objectMapper)
            }

            "${orders.lastOrNull()?.orderId?: 0}" to orders
        }

    }
}
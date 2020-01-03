package com.onepiece.gpgaming.games.combination

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.BetOrder
import com.onepiece.gpgaming.beans.model.token.AsiaGamingClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.DesECBUtil
import com.onepiece.gpgaming.games.bet.MapUtil
import com.onepiece.gpgaming.utils.StringUtil
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.time.format.DateTimeFormatter

@Service
class AsiaGamingService : PlatformService() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")


    fun startGetXml(data: List<String>, clientToken: AsiaGamingClientToken): AsiaGamingValue.Result {


        val signData = data.joinToString(separator = "/\\\\/")
        val params = DesECBUtil.encrypt(data = signData, key = clientToken.desSecret)
//                .let { URLEncoder.encode(it, "utf-8") }
        val key = DigestUtils.md5Hex("$params${clientToken.md5Secret}")

        val headers = mapOf(
                "headers" to "WEB_LIB_GI_${clientToken.agentCode}"
        )
        val apiPath = "${clientToken.apiDomain}/doBusiness.do?params=${params.let { URLEncoder.encode(it, "utf-8") }}&key=$key"
        return okHttpUtil.doGetXml(url = apiPath, clz = AsiaGamingValue.Result::class.java, headers = headers)
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as AsiaGamingClientToken

        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${registerReq.username}",
                "method=lg",
                "actype=1", // 1 真钱 2 试玩
                "password=${registerReq.password}",
                "oddtype=A",
                "cur=${clientToken.currency}"
        )

        val result = this.startGetXml(data = data, clientToken = clientToken)
        check(result.info == "0") { result.msg }


        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as AsiaGamingClientToken


        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${balanceReq.username}",
                "method=gb",
                "actype=1",
                "password=${balanceReq.password}",
                "cur=${clientToken.currency}"
        )

        val result = this.startGetXml(data = data, clientToken = clientToken)
        return try {
            result.info.toBigDecimal()
        } catch (e: Exception) {
            error(result.msg)
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {

        val clientToken = transferReq.token as AsiaGamingClientToken

        val amount = when (clientToken.currency) {
            "IDR", "VND" -> transferReq.amount.abs().div(BigDecimal.valueOf(1000))
            else -> transferReq.amount.abs()
        }

        val type = if (transferReq.amount.toDouble() > 0) "IN" else "OUT"
        val preData = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${transferReq.username}",
                "method=tc",
                "billno=${transferReq.orderId}",
                "type=$type",
                "credit=${amount}",
                "actype=1",
                "password=${transferReq.password}",
                "cur=${clientToken.currency}"
        )
        val preResult = this.startGetXml(data = preData, clientToken = clientToken)
        check(preResult.info == "0") { preResult.msg }


        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${transferReq.username}",
                "method=tcc",
                "billno=${transferReq.orderId}",
                "type=$type",
                "credit=${amount}",
                "actype=1",
                "flag=1",
                "password=${transferReq.password}",
                "cur=${clientToken.currency}"
        )
        val result = this.startGetXml(data = data, clientToken = clientToken)
        check(result.info == "0") { result.msg }

        return GameValue.TransferResp.successful(platformOrderId = transferReq.orderId)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {

        val clientToken = checkTransferReq.token as AsiaGamingClientToken

        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "billno=${checkTransferReq.orderId}",
                "method=qos",
                "actype=1",
                "cur=${clientToken.currency}"
        )
        val result = this.startGetXml(data = data, clientToken = clientToken)
        check(result.info == "0") { result.msg }

        return GameValue.TransferResp.successful(platformOrderId = checkTransferReq.orderId)
    }

    private fun getLang(language: Language): String {
        return when (language) {
            Language.CN -> "1"
            Language.EN -> "3"
            Language.ID -> "11"
            Language.TH -> "6"
            Language.VI -> "8"
            else -> "3"
        }
    }

    override fun startSlot(startSlotReq: GameValue.StartSlotReq): String {

        val clientToken = startSlotReq.token as AsiaGamingClientToken

        val mh5 = if (startSlotReq.launchMethod == LaunchMethod.Wap) "mh5=y" else ""
        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${startSlotReq.username}",
                "actype=1",
                "password=${startSlotReq.password}",
                "dm=${startSlotReq.redirectUrl}",
                "sid=${clientToken.agentCode}${StringUtil.generateNumNonce(15)}",
                "lang=${getLang(startSlotReq.language)}",
                "gameType=${startSlotReq.gameId}",
                "oddtype=A",
                "cur=${clientToken.currency}",
                mh5
        ).filter { it.isNotBlank() }

        val signData = data.joinToString(separator = "/\\\\/")
        val params = DesECBUtil.encrypt(data = signData, key = clientToken.desSecret)
        val key = DigestUtils.md5Hex("$params${clientToken.md5Secret}")


        return "${clientToken.startGameApiDomain}/forwardGame.do?params=${params}&key=${key}"
    }

    override fun start(startReq: GameValue.StartReq): String {
        val clientToken = startReq.token as AsiaGamingClientToken

        val mh5 = if (startReq.launch == LaunchMethod.Wap) "mh5=y" else ""
        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${startReq.username}",
                "actype=1",
                "password=${startReq.password}",
                "dm=${startReq.redirectUrl}",
                "sid=${clientToken.agentCode}${StringUtil.generateNumNonce(15)}",
                "lang=${getLang(startReq.language)}",
//                "gameType="
                "oddtype=A",
                "cur=${clientToken.currency}",
                mh5
        ).filter { it.isNotBlank() }

        val signData = data.joinToString(separator = "/\\\\/")
        val params = DesECBUtil.encrypt(data = signData, key = clientToken.desSecret)
        val key = DigestUtils.md5Hex("$params${clientToken.md5Secret}")


        return "${clientToken.startGameApiDomain}/forwardGame.do?params=${params}&key=${key}"
    }


    fun handleBet(): (platform: Platform, bet: MapUtil) -> BetOrderValue.BetOrderCo {
        return { platform, bet ->
            when (platform) {
                Platform.AsiaGamingLive -> {
                    val orderId = bet.asString("billNo")
                    val username = bet.asString("playName")

                    val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = platform, platformUsername = username)
                    val betTime = bet.asLocalDateTime("betTime", dateTimeFormatter)
                    val betAmount = bet.asBigDecimal("betAmount")

                    val netAmount = bet.asBigDecimal("netAmount")
                    val winAmount = betAmount.plus(netAmount)

                    val originData = objectMapper.writeValueAsString(bet.data)
                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = platform, betAmount = betAmount, winAmount = winAmount,
                            betTime = betTime, settleTime = betTime, orderId = orderId, originData = originData)
                }
                Platform.AsiaGamingSlot -> {

                    val orderId = bet.asString("billno")
                    val username = bet.asString("username")
                    val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = platform, platformUsername = username)
                    val betTime = bet.asLocalDateTime("billtime", dateTimeFormatter)
                    val settleTime = bet.asLocalDateTime("reckontime", dateTimeFormatter)
                    val betAmount = bet.asBigDecimal("account")
                    val winAmount = bet.asBigDecimal("cus_account")

                    val originData = objectMapper.writeValueAsString(bet.data)
                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = platform, betAmount = betAmount, winAmount = winAmount,
                            betTime = betTime, settleTime = betTime, orderId = orderId, originData = originData)

                }
                else -> error("错误的平台: $platform")
            }
        }
    }


    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {

        val clientToken = pullBetOrderReq.token as AsiaGamingClientToken

        val path = if (pullBetOrderReq.platform == Platform.AsiaGamingLive) "getorders.xml" else "getslotorders_ex.xml"
        val startTime = pullBetOrderReq.startTime.minusHours(12).format(dateTimeFormatter)
        val endTime = pullBetOrderReq.endTime.minusHours(12).format(dateTimeFormatter)

        val orders = arrayListOf<BetOrderValue.BetOrderCo>()
        var page = 1
        var totalPage = 1

        do {
            val data = mapOf(
                    "cagent" to clientToken.orderAgentCode,
                    "startdate" to startTime,
                    "enddate" to endTime,
                    "order" to "billno",
                    "by" to "ASC",
                    "page" to page,
                    "perpage" to "500"
            )

            val signParam = data.map { it.value }.plus(clientToken.orderMd5Secret).joinToString(separator = "")
            val sign = DigestUtils.md5Hex(signParam)

            val urlParam = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")

            val url =  "http://gdpfb8.gdcapi.com:3333/${path}?$urlParam&key=$sign"
            val result = okHttpUtil.doGetXml(url = url, clz = AsiaGamingValue.BetResult::class.java)

            val list = result.row.map {  row ->
                val bet = row.mapUtil
                val handle = handleBet()
                handle(pullBetOrderReq.platform, bet)
            }

            orders.addAll(list)

            page += 1
            totalPage = result.addition.totalpage
        } while (totalPage > page)

        return orders
    }
}






















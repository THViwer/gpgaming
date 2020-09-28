package com.onepiece.gpgaming.games.combination

import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.U9RequestStatus
import com.onepiece.gpgaming.beans.model.token.AsiaGamingClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.DesECBUtil
import com.onepiece.gpgaming.games.bet.MapUtil
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import com.onepiece.gpgaming.utils.StringUtil
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.time.format.DateTimeFormatter

@Service
class AsiaGamingService : PlatformService() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private val log = LoggerFactory.getLogger(AsiaGamingService::class.java)

    fun doGetXml(data: List<String>, clientToken: AsiaGamingClientToken): OKResponse {
        val signData = data.joinToString(separator = "/\\\\/")
        val params = DesECBUtil.encrypt(data = signData, key = clientToken.desSecret)
        val key = DigestUtils.md5Hex("$params${clientToken.md5Secret}")

        val headers = mapOf(
                "headers" to "WEB_LIB_GI_${clientToken.agentCode}"
        )

        val url = "${clientToken.apiPath}/doBusiness.do"
        val param = "params=${params.let { URLEncoder.encode(it, "utf-8") }}&key=$key"

        val okParam = OKParam.ofGetXml(url = url, param = param, headers = headers)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)
        if (!okResponse.ok) return okResponse

        return okResponse
    }


    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val clientToken = registerReq.token as AsiaGamingClientToken

        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${registerReq.username}",
                "method=lg",
                "actype=1", // 1 真钱 2 试玩
                "password=${registerReq.password}",
                "oddtype=C",
                "cur=${clientToken.currency}"
        )

        val okResponse = this.doGetXml(data = data, clientToken = clientToken)

        val status = try {
            when (okResponse.asInt("info")) {
                0 -> U9RequestStatus.OK
                else -> U9RequestStatus.Fail
            }
        } catch (e: Exception) {
            U9RequestStatus.Fail
        }
        return this.bindGameResponse(okResponse = okResponse.copy(status = status)) {
            registerReq.username
        }
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as AsiaGamingClientToken

        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${balanceReq.username}",
                "method=gb",
                "actype=1",
                "password=${balanceReq.password}",
                "cur=${clientToken.currency}"
        )

        val okResponse = this.doGetXml(data = data, clientToken = clientToken)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asBigDecimal("info")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {

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
        val preOkResponse = this.doGetXml(data = preData, clientToken = clientToken)
        val preStatus = try {
            when (preOkResponse.asInt("info")) {
                0 -> U9RequestStatus.OK
                else -> U9RequestStatus.Fail
            }
        } catch (e: Exception) {
            U9RequestStatus.Fail
        }
        if (preStatus != U9RequestStatus.OK) return this.bindGameResponse(okResponse = preOkResponse.copy(status = preStatus)) {
            GameValue.TransferResp.failed()
        }

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
        val okResponse = this.doGetXml(data = data, clientToken = clientToken)
        val status = try {
            when (okResponse.asInt("info")) {
                0 -> U9RequestStatus.OK
                else -> U9RequestStatus.Fail
            }
        } catch (e: Exception) {
            U9RequestStatus.Fail
        }
        return this.bindGameResponse(okResponse = okResponse.copy(status = status)) {
            GameValue.TransferResp.successful()
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {

        val clientToken = checkTransferReq.token as AsiaGamingClientToken

        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "billno=${checkTransferReq.orderId}",
                "method=qos",
                "actype=1",
                "cur=${clientToken.currency}"
        )
        val okResponse = this.doGetXml(data = data, clientToken = clientToken)
        val status = try {
            when (okResponse.asInt("info")) {
                0 -> U9RequestStatus.OK
                else -> U9RequestStatus.Fail
            }
        } catch (e: Exception) {
            U9RequestStatus.Fail
        }
        return this.bindGameResponse(okResponse = okResponse.copy(status = status)) {
            GameValue.TransferResp.successful()
        }
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

    override fun startSlot(startSlotReq: GameValue.StartSlotReq): GameResponse<String> {

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

        val path = "${clientToken.gamePath}/forwardGame.do?params=${params}&key=${key}"
        return GameResponse.of(data = path)
    }

    override fun start(startReq: GameValue.StartReq): GameResponse<String> {
        val clientToken = startReq.token as AsiaGamingClientToken

        val mh5 = if (startReq.launch == LaunchMethod.Wap) "mh5=y" else ""
        val gameType = if (startReq.launch == LaunchMethod.Wap) "0" else "18"
        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${startReq.username}",
                "actype=1",
                "password=${startReq.password}",
                "dm=${startReq.redirectUrl}",
                "sid=${clientToken.agentCode}${StringUtil.generateNumNonce(15)}",
                "lang=${getLang(startReq.language)}",
                "gameType=$gameType",
                "oddtype=A",
                "cur=${clientToken.currency}",
                mh5
        ).filter { it.isNotBlank() }

        val signData = data.joinToString(separator = "/\\\\/")
        val params = DesECBUtil.encrypt(data = signData, key = clientToken.desSecret)
        val key = DigestUtils.md5Hex("$params${clientToken.md5Secret}")

        val path = "${clientToken.gamePath}/forwardGame.do?params=${params}&key=${key}"
        return GameResponse.of(data = path)
    }


    fun handleBet(): (platform: Platform, bet: MapUtil) -> BetOrderValue.BetOrderCo {
        return { platform, bet ->
            when (platform) {
                Platform.AsiaGamingLive -> {
                    val orderId = bet.asString("billNo")
                    val username = bet.asString("playName")

                    val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = platform, platformUsername = username)
                    val betTime = bet.asLocalDateTime("betTime", dateTimeFormatter).plusHours(12) // +12 hour 使用UTC-8时区
                    val betAmount = bet.asBigDecimal("betAmount")
                    val validBetAmount = bet.asBigDecimal("validBetAmount")

                    val netAmount = bet.asBigDecimal("netAmount")
                    val winAmount = betAmount.plus(netAmount)

                    val originData = objectMapper.writeValueAsString(bet.data)
                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = platform, betAmount = betAmount, winAmount = winAmount,
                            betTime = betTime, settleTime = betTime, orderId = orderId, originData = originData, validAmount = validBetAmount)
                }
                Platform.AsiaGamingSlot -> {

                    val orderId = bet.asString("billno")
                    val username = bet.asString("username")
                    val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = platform, platformUsername = username)
                    val betTime = bet.asLocalDateTime("billtime", dateTimeFormatter).plusHours(12) // +12 hour 使用UTC-8时区
                    val settleTime = bet.asLocalDateTime("reckontime", dateTimeFormatter).plusHours(12) // +12 hour 使用UTC-8时区
                    val betAmount = bet.asBigDecimal("account")
                    val validAccount = bet.asBigDecimal("valid_account")
                    val winAmount = bet.asBigDecimal("cus_account")

                    val originData = objectMapper.writeValueAsString(bet.data)
                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = platform, betAmount = betAmount, winAmount = winAmount,
                            betTime = betTime, settleTime = settleTime, orderId = orderId, originData = originData, validAmount = validAccount)

                }
                else -> error("错误的平台: $platform")
            }
        }
    }


    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {

        val clientToken = pullBetOrderReq.token as AsiaGamingClientToken

        val path = if (pullBetOrderReq.platform == Platform.AsiaGamingLive) "getorders.xml" else "getslotorders_ex.xml"
        val startTime = pullBetOrderReq.startTime.minusHours(12).format(dateTimeFormatter)
        val endTime = pullBetOrderReq.endTime.minusHours(12).format(dateTimeFormatter)

        val orders = arrayListOf<BetOrderValue.BetOrderCo>()
        var page = 1
        var totalPage = 1

        var gameResponse: GameResponse<List<BetOrderValue.BetOrderCo>>
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

            val url = "http://gdpfb8.gdcapi.com:3333/${path}"
            val param = "$urlParam&key=$sign"

            val okParam = OKParam.ofGetXml(url = url, param = param)
            val okResponse = u9HttpRequest.startRequest(okParam)

            gameResponse = this.bindGameResponse(okResponse = okResponse) {
                val result = xmlMapper.readValue<AsiaGamingValue.BetResult>(okResponse.response)
                val list = result.row.map { row ->
                    val bet = row.mapUtil
                    val handle = handleBet()
                    handle(pullBetOrderReq.platform, bet)
                }

                orders.addAll(list)

                page += 1
                totalPage = result.addition.totalpage

                list
            }

        } while (totalPage > page)

        return gameResponse.copy(data = orders)
    }
}






















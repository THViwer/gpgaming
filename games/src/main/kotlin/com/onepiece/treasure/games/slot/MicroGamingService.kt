package com.onepiece.treasure.games.slot

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.token.ClientToken
import com.onepiece.treasure.beans.model.token.MicroGamingClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.MapUtil
import okhttp3.FormBody
import org.apache.commons.codec.binary.Base64
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@Service
class MicroGamingService : PlatformService() {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    private val betDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    private fun getOauthToken(clientToken: MicroGamingClientToken): String {

        val redisKey = OnePieceRedisKeyConstant.getMicroGameToken(clientToken.username)

        return redisService.get(key = redisKey, clz = String::class.java, timeout = 3000) {

            val authorization = Base64.encodeBase64String("${clientToken.authUsername}:${clientToken.authPassword}".toByteArray())

            val headers = mapOf(
                    "Authorization" to "Basic $authorization",
                    "X-DAS-TZ" to "UTC+8",
                    "X-DAS-CURRENCY" to "MYR",
                    "X-DAS-TX-ID" to "TEXT-TX-ID",
                    "X-DAS-LANG" to "en"
            )

            val body = FormBody.Builder()
                    .add("grant_type", "password")
                    .add("username", clientToken.username)
                    .add("password", clientToken.password)
                    .build()

            val url = "${gameConstant.getDomain(Platform.MicroGaming)}/oauth/token"
            val oauthToken = okHttpUtil.doPostForm(url = url, body = body, headers = headers, clz = MicroGamingValue.OauthToken::class.java)
            oauthToken.access_token
        }!!
    }

    fun startPostJson(clientToken: MicroGamingClientToken, method: String, data: String): MapUtil {

        val accessToken = this.getOauthToken(clientToken)
        val header = mapOf(
                "Authorization" to "Bearer $accessToken",
                "X-DAS-TZ" to "UTC+8",
                "X-DAS-CURRENCY" to "MYR",
                "X-DAS-TX-ID" to "TEXT-TX-ID",
                "X-DAS-LANG" to "en"
        )

        val url = "${gameConstant.getDomain(Platform.MicroGaming)}$method"
        val result = okHttpUtil.doPostJson(url = url, headers = header, data = data, clz = MicroGamingValue.Result::class.java)
        return result.mapUtil
    }

    fun startGetJson(clientToken: MicroGamingClientToken, method: String, urlParam: String): MapUtil {

        val accessToken = this.getOauthToken(clientToken)
        val header = mapOf(
                "Authorization" to "Bearer $accessToken",
                "X-DAS-TZ" to "UTC+8",
                "X-DAS-CURRENCY" to "MYR",
                "X-DAS-TX-ID" to "TEXT-TX-ID",
                "X-DAS-LANG" to "en"
        )

        val url = "${gameConstant.getDomain(Platform.MicroGaming)}$method?$urlParam"
        val result = okHttpUtil.doGet(url = url, headers = header, clz = MicroGamingValue.Result::class.java)
        return result.mapUtil
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as MicroGamingClientToken

        val data = """
            {
                "parent_id": "${clientToken.parentId}",
                "username": "${registerReq.username}",
                "password": "${registerReq.password}",
                "ext_ref": "${registerReq.username}"
            }
        """.trimIndent()
        this.startPostJson(method = "/v1/account/member", clientToken = clientToken, data = data)
        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as MicroGamingClientToken

        val mapUtil = this.startGetJson(clientToken = clientToken, method = "/v1/wallet", urlParam = "account_ext_ref=${balanceReq.username}")
        return mapUtil.asList("data").first().asBigDecimal("cash_balance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        val clientToken = transferReq.token as MicroGamingClientToken

        val type = if (transferReq.amount.toDouble() > 0) "CREDIT" else "DEBIT"
        val data = """
            [{
                "account_ext_ref": "${transferReq.username}",
                "category": "ADJUSTMENT",
                "type": "$type",
                "amount": ${transferReq.amount.abs()},
                "external_ref": "${transferReq.orderId}"
            }]
        """.trimIndent()

        val mapUtil = this.startPostJson(method = "/v1/transaction", clientToken = clientToken, data = data)
        return mapUtil.asList("data").first().asString("parent_transaction_id")
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val clientToken = checkTransferReq.token as MicroGamingClientToken
        val urlParam = "ext_ref=${checkTransferReq.orderId}&account_ext_ref=${checkTransferReq.username}"
        val mapUtil = this.startGetJson(clientToken = clientToken, method = "/v1/transaction", urlParam = urlParam)
        return mapUtil.asList("data").firstOrNull() != null
    }

    override fun startDemo(token: ClientToken, language: Language): String {
        val clientToken = token as MicroGamingClientToken

        val lang = when (language) {
            Language.EN -> "en_US"
            Language.VI -> "vi_VN"
            Language.ID -> "in_ID"
            Language.TH -> "th_TH"
            Language.CN -> "zh_CN"
            Language.MY -> "ms_MY"
            else -> "en_US"
        }

        val data = """
            {
                "item_id": 1389,
                "app_id": 1002,
                "demo": false,
                "login_context": {
                    "lang": "$lang"
                }
            }
        """.trimIndent()
        val mapUtil = this.startPostJson(clientToken = clientToken, method = "/v1/launcher/item", data = data)
        return mapUtil.asString("data")
    }

    override fun slotGames(token: ClientToken, launch: LaunchMethod): List<SlotGame> {
        return emptyList()
    }


    override fun startSlot(startSlotReq: GameValue.StartSlotReq): String {
        val clientToken = startSlotReq.token as MicroGamingClientToken

        val (itemId, appId) = startSlotReq.gameId.split("_")

        val lang = when (startSlotReq.language) {
            Language.EN -> "en_US"
            Language.VI -> "vi_VN"
            Language.ID -> "in_ID"
            Language.TH -> "th_TH"
            Language.CN -> "zh_CN"
            Language.MY -> "ms_MY"
            else -> "en_US"
        }
        val data = """
            {
                "ext_ref": "${startSlotReq.username}",
                "item_id": ${itemId},
                "app_id": ${appId},
                "demo": false,
                "login_context": {
                    "lang": "$lang"
                },
                "conf_params": {
                    "lobby_url": "${startSlotReq.redirectUrl}",
                    "banking_url": "${startSlotReq.redirectUrl}",
                    "logout_url": "${startSlotReq.redirectUrl}",
                    "failed_url": "${startSlotReq.redirectUrl}"
                }
            }
        """.trimIndent()
        val mapUtil = this.startPostJson(clientToken = clientToken, method = "/v1/launcher/item", data = data)
        return mapUtil.asString("data")
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val clientToken = pullBetOrderReq.token as MicroGamingClientToken

        val data = listOf(
                "company_id=131362069",
                "include_transfers=false",
                "start_time=${pullBetOrderReq.startTime.format(dateTimeFormat)}",
                "end_time=${pullBetOrderReq.endTime.format(dateTimeFormat)}",
                "page=1",
                "page_size=100000"
        )

        val urlParam = data.joinToString(separator = "&")
        val mapUtil = this.startGetJson(clientToken = clientToken, method = "/v1/feed/transaction", urlParam = urlParam)

        return mapUtil.asList("data").map { bet ->

            val orderId = bet.asString("external_ref")
            val username = bet.asString("account_ext_ref")
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.MicroGaming, platformUsername = username)
            val category = bet.asString("category")
            val amount = bet.asBigDecimal("amount")
            val betAmount: BigDecimal
            val winAmount: BigDecimal
            if (category == "WAGER") {
                betAmount = amount
                winAmount = BigDecimal.ZERO
            } else {
                betAmount = BigDecimal.ZERO
                winAmount = amount
            }
            val betTime = bet.asLocalDateTime("transaction_time", betDateTimeFormat)

            val originData = objectMapper.writeValueAsString(bet.data)
            BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, platform = Platform.MicroGaming, betAmount = betAmount,
                    winAmount = winAmount, betTime = betTime, settleTime = betTime, originData = originData)
        }
    }
}
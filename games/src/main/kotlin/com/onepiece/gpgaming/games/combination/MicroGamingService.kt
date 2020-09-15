package com.onepiece.gpgaming.games.combination

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.token.MicroGamingClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@Service
class MicroGamingService : PlatformService() {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    private val betDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    private val log = LoggerFactory.getLogger(MicroGamingService::class.java)

    private fun getOauthToken(clientToken: MicroGamingClientToken): String {

        val redisKey = OnePieceRedisKeyConstant.getMicroGameToken(clientToken.username)

        return redisService.get(key = redisKey, clz = String::class.java, timeout = 3000) {

            val authorization = Base64.encodeBase64String("${clientToken.authUsername}:${clientToken.authPassword}".toByteArray())

            val headers = mapOf(
                    "Authorization" to "Basic $authorization",
                    "X-DAS-TZ" to "UTC+8",
                    "X-DAS-CURRENCY" to clientToken.currency,
                    "X-DAS-TX-ID" to "TEXT-TX-ID",
                    "X-DAS-LANG" to "en"
            )

            val param = mapOf(
                    "grant_type" to "password",
                    "username" to clientToken.username,
                    "password" to clientToken.password
            )

            val url = "${clientToken.apiPath}/oauth/token"

            val okParam = OKParam.ofPost(url = url, headers = headers, param = "", formParam = param)
            val okResponse = u9HttpRequest.startRequest(okParam = okParam)
            check(okResponse.ok)

            okResponse.asString("access_token")
        }!!
    }

    fun doPost(clientToken: MicroGamingClientToken, method: String, data: String): OKResponse {

        val accessToken = this.getOauthToken(clientToken)
        val header = mapOf(
                "Authorization" to "Bearer $accessToken",
                "X-DAS-TZ" to "UTC+8",
                "X-DAS-CURRENCY" to clientToken.currency,
                "X-DAS-TX-ID" to "TEXT-TX-ID",
                "X-DAS-LANG" to "en"
        )

        val url = "${clientToken.apiPath}$method"

        val okParam = OKParam.ofPost(url = url, param = data, headers = header)
        return u9HttpRequest.startRequest(okParam = okParam)
    }


    fun doGet(clientToken: MicroGamingClientToken, method: String, urlParam: String): OKResponse {

        val accessToken = this.getOauthToken(clientToken)
        val header = mapOf(
                "Authorization" to "Bearer $accessToken",
                "X-DAS-TZ" to "UTC+8",
                "X-DAS-CURRENCY" to clientToken.currency,
                "X-DAS-TX-ID" to "TEXT-TX-ID",
                "X-DAS-LANG" to "en"
        )

        val url = "${clientToken.apiPath}$method"
        val okParam = OKParam.ofGet(url = url, param = urlParam, headers = header)
        return u9HttpRequest.startRequest(okParam = okParam)
    }


    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val clientToken = registerReq.token as MicroGamingClientToken

        val data = """
            {
                "parent_id": "${clientToken.parentId}",
                "username": "${registerReq.username}",
                "password": "${registerReq.password}",
                "ext_ref": "${registerReq.username}"
            }
        """.trimIndent()
        val okResponse = this.doPost(method = "/v1/account/member", clientToken = clientToken, data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            registerReq.username
        }
    }

    override fun updatePassword(updatePasswordReq: GameValue.UpdatePasswordReq): GameResponse<Unit> {
        val clientToken = updatePasswordReq.token as MicroGamingClientToken

        val data = """
            {
                "ext_ref": "${updatePasswordReq.username}",
                "password": "${updatePasswordReq.password}"
            }
        """.trimIndent()
        val okResponse = this.doPost(method = "/v1/account/member/password", clientToken = clientToken, data = data)
        return this.bindGameResponse(okResponse = okResponse) {}
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as MicroGamingClientToken

        val okResponse = this.doGet(clientToken = clientToken, method = "/v1/wallet", urlParam = "account_ext_ref=${balanceReq.username}")
        return this.bindGameResponse(okResponse = okResponse) {
            it.asList("data").first().asBigDecimal("cash_balance")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
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

        val okResponse = this.doPost(method = "/v1/transaction", clientToken = clientToken, data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            val platformOrderId = it.asList("data").first().asString("parent_transaction_id")
            val balance = it.asList("data").first().asBigDecimal("balance")
            GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
        }

    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = checkTransferReq.token as MicroGamingClientToken
        val urlParam = "ext_ref=${checkTransferReq.orderId}&account_ext_ref=${checkTransferReq.username}"
        val okResponse = this.doGet(clientToken = clientToken, method = "/v1/transaction", urlParam = urlParam)

        return this.bindGameResponse(okResponse = okResponse) {
            val successful = it.asList("data").firstOrNull() != null
            val balance = it.asList("data").firstOrNull()?.asBigDecimal("balance") ?: BigDecimal.valueOf(-1)
            GameValue.TransferResp.of(successful = successful, balance = balance)
        }

    }

    override fun start(startReq: GameValue.StartReq): GameResponse<String> {

        // 70577
        val itemId = 1930
        val appId = 1001

        val clientToken = startReq.token as MicroGamingClientToken


        val lang = when (startReq.language) {
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
                "ext_ref": "${startReq.username}",
                "item_id": ${itemId},
                "app_id": ${appId},
                "demo": false,
                "login_context": {
                    "lang": "$lang"
                },
                "conf_params": {
                    "titanium": "default",
                    "lobby_url": "${startReq.redirectUrl}",
                    "banking_url": "${startReq.redirectUrl}",
                    "logout_url": "${startReq.redirectUrl}",
                    "failed_url": "${startReq.redirectUrl}"
                }
            }
        """.trimIndent()
        val okResponse = this.doPost(clientToken = clientToken, method = "/v1/launcher/item", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asString("data")
        }

    }


    override fun startSlotDemo(startSlotReq: GameValue.StartSlotReq): GameResponse<String> {

        val clientToken = startSlotReq.token as MicroGamingClientToken

        val lang = when (startSlotReq.language) {
            Language.EN -> "en_US"
            Language.VI -> "vi_VN"
            Language.ID -> "in_ID"
            Language.TH -> "th_TH"
            Language.CN -> "zh_CN"
            Language.MY -> "ms_MY"
            else -> "en_US"
        }

        val (itemId, appId) = startSlotReq.gameId.split("_")
        val data = """
            {
                "item_id": ${itemId},
                "app_id": ${appId},
                "demo": true,
                "login_context": {
                    "lang": "$lang"
                }
            }
        """.trimIndent()
        val okResponse = this.doPost(clientToken = clientToken, method = "/v1/launcher/item", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asString("data")
        }
    }


    override fun startSlot(startSlotReq: GameValue.StartSlotReq): GameResponse<String> {
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
        val okResponse = this.doPost(clientToken = clientToken, method = "/v1/launcher/item", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asString("data")
        }
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {
        val clientToken = pullBetOrderReq.token as MicroGamingClientToken

        val data = listOf(
                "company_id=${clientToken.parentId}",
                "include_transfers=false",
                "start_time=${pullBetOrderReq.startTime.format(dateTimeFormat)}",
                "end_time=${pullBetOrderReq.endTime.format(dateTimeFormat)}",
                "page=1",
                "page_size=100000"
        )

        val urlParam = data.joinToString(separator = "&")
        val okResponse = this.doGet(clientToken = clientToken, method = "/v1/feed/transaction", urlParam = urlParam)

        return this.bindGameResponse(okResponse = okResponse) { mapUtil ->
            mapUtil.asList("data").map { bet ->

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

                val platform = when (bet.asMap("meta_data").asString("item_id")) {
                    "1930", "1921", "2048", "2049", "1931", "1922", "1936", "1912" -> Platform.MicroGamingLive
                    else -> Platform.MicroGaming
                }


                val originData = objectMapper.writeValueAsString(bet.data)
                BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, platform = platform, betAmount = betAmount,
                        winAmount = winAmount, betTime = betTime, settleTime = betTime, originData = originData, validAmount = betAmount)
            }
        }


    }
}
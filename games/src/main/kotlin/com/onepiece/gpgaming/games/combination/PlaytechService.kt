package com.onepiece.gpgaming.games.combination

import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.token.PlaytechClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.ActiveConfig
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import com.onepiece.gpgaming.utils.StringUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@Service
class PlaytechService(
        private val activeConfig: ActiveConfig
) : PlatformService() {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val log = LoggerFactory.getLogger(PlaytechService::class.java)

    fun doPost(clientToken: PlaytechClientToken, path: String, data: String): OKResponse {
        val url = "${clientToken.apiPath}${path}"
        val headers = mapOf(
                "X-Auth-Api-Key" to clientToken.accessToken
        )

        val okParam = OKParam.ofPost(url = url, param = data, headers = headers)
        return u9HttpRequest.startRequest(okParam = okParam)
    }

    fun doGet(clientToken: PlaytechClientToken, path: String, data: List<String>): OKResponse {
        val urlParam = data.joinToString(separator = "&")
        val url = "${clientToken.apiPath}${path}"
        val headers = mapOf(
                "X-Auth-Api-Key" to clientToken.accessToken
        )

        val okParam = OKParam.ofGet(url = url, param = urlParam, headers = headers)
        return u9HttpRequest.startRequest(okParam = okParam)
    }

    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val clientToken = registerReq.token as PlaytechClientToken

        val name = if (registerReq.name.length < 6) "${registerReq.name}${StringUtil.generateNonce(3)}" else registerReq.name
        val data = """
            {
                "name": "$name",
                "username": "${registerReq.username}",
                "password": "${registerReq.password}",
                "kiosk_name": "${clientToken.agentName}"
            }
        """.trimIndent()

        val okResponse = this.doPost(clientToken = clientToken, path = "/backoffice/player/create", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            registerReq.username
        }
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as PlaytechClientToken

        val data = listOf(
//                "player_name=${clientToken.prefix}_${balanceReq.username}",
                "player_name=${clientToken.prefix}_${balanceReq.username}",
                "server_name=${clientToken.serverName}"
        )
        val okResponse = this.doGet(clientToken = clientToken, path = "/backoffice/player/serverBalance", data = data)

        val ok = okResponse.asInt("code") == 200
        return this.bindGameResponse(okResponse = okResponse.copy(ok = ok)) {
            val wallet = clientToken.serverName
            it.asMap("data").asMap("wallets").asBigDecimal(wallet)
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = transferReq.token as PlaytechClientToken

        val toPlayer = "${clientToken.prefix}_${transferReq.username}".toUpperCase()
        val okResponse = when (transferReq.amount.toDouble() > 0) {
            true -> {
                val data = """
                    {
                        "from_admin": "${clientToken.admin}",
                        "to_player": "$toPlayer",
                        "currency": "${clientToken.currency}",
                        "amount": ${transferReq.amount},
                        "server": "${clientToken.serverName}",
                        "client_reference_no": "${transferReq.orderId}"
                    }
                """.trimIndent()

                this.doPost(clientToken = clientToken, path = "/backoffice/transfer/player/deposit", data = data)
            }
            false -> {
                val data = """
                    {
                        "from_player": "$toPlayer",
                        "to_admin": "${clientToken.admin}",
                        "currency": "${clientToken.currency}",
                        "amount": ${transferReq.amount.abs()},
                        "is_forced": 1,
                        "server": "${clientToken.serverName}",
                        "client_reference_no": "${transferReq.orderId}"
                    }
                """.trimIndent()

                this.doPost(clientToken = clientToken, path = "/backoffice/transfer/player/withdraw", data = data)
            }
        }

        val ok = okResponse.asInt("code") == 200
        return this.bindGameResponse(okResponse = okResponse.copy(ok = ok)) {
            val platformOrderId = it.asMap("data").asString("reference_no")
            GameValue.TransferResp.successful(platformOrderId = platformOrderId)
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = checkTransferReq.token as PlaytechClientToken

        val data = listOf(
                "reference_no=${checkTransferReq.platformOrderId}",
                "client_reference_no=${checkTransferReq.orderId}"
        )
        val okResponse = this.doGet(clientToken = clientToken, path = "/backoffice/transfer/player/status", data = data)

        val ok = okResponse.asInt("code") == 200
        return this.bindGameResponse(okResponse = okResponse.copy(ok = ok)) {
            GameValue.TransferResp.successful()
        }
    }


    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {
        val clientToken = pullBetOrderReq.token as PlaytechClientToken

        var page = 1
        var hasNextPage = false
        val orders = arrayListOf<BetOrderValue.BetOrderCo>()

        var gameResponse = GameResponse.of(data = emptyList<BetOrderValue.BetOrderCo>())
        do {
            val data = listOf(
                    "game_server=${clientToken.serverName}",
                    "date_from=${pullBetOrderReq.startTime.format(dateTimeFormat)}",
                    "date_to=${pullBetOrderReq.endTime.format(dateTimeFormat)}",
                    "page=${page}"
            )

            val urlParam = data.joinToString(separator = "&")
            val url = "${clientToken.apiPath}/backoffice/reports/gameTransactions?$urlParam"
            val headers = mapOf(
                    "X-Auth-Api-Key" to clientToken.accessToken
            )

            val okParam = OKParam.ofGet(url = url, param = urlParam, headers = headers)
            val okResponse = u9HttpRequest.startRequest(okParam = okParam)

            gameResponse = this.bindGameResponse(okResponse = okResponse) {

                val result = okResponse.response.let {
                    objectMapper.readValue<PlaytechValue.BetResult>(it)
                }

                val list = result.data.orders.map { bet ->
                    val orderId = bet.asString("game_server_reference_1")
                    val betAmount = bet.asBigDecimal("bet")
                    val winAmount = bet.asBigDecimal("win")
                    val betTime = bet.asLocalDateTime("bet_datetime", dateTimeFormat)
                    val username = bet.asString("gamzo_player_name").split("_")[1]
                    val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = pullBetOrderReq.platform,
                            platformUsername = username, prefix = "${clientToken.prefix}_")

                    val originData = objectMapper.writeValueAsString(bet.data)

                    val gameType = bet.asString("game_type")
                    val platform = if (gameType.toLowerCase().contains("slot")) Platform.PlaytechSlot else Platform.PlaytechLive

                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = platform, orderId = orderId, betAmount = betAmount,
                            winAmount = winAmount, originData = originData, betTime = betTime, settleTime = betTime, validAmount = betAmount)
                }

                orders.addAll(list)

                page += 1
                hasNextPage = result.data.pagination.has_next_page

                list
            }

        } while (hasNextPage)

        return gameResponse.copy(data = orders)
    }


}
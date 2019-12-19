package com.onepiece.gpgaming.games.combination

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.PlaytechSlotClientToken
import com.onepiece.gpgaming.games.ActiveConfig
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PlaytechService(
        private val activeConfig: ActiveConfig
) : PlatformService() {

    fun startPostJson(clientToken: PlaytechSlotClientToken, path: String, data: String): PlaytechValue.Result {
        val url = "${gameConstant.getDomain(Platform.PlaytechSlot)}${path}"
        val headers = mapOf(
                "X-Auth-Api-Key" to clientToken.accessToken
        )
        return  okHttpUtil.doPostJson(url = url, data = data, headers = headers, clz = PlaytechValue.Result::class.java)
    }

    fun startGetJson(clientToken: PlaytechSlotClientToken, path: String, data: List<String>): PlaytechValue.Result {
        val urlParam = data.joinToString(separator = "&")
        val url = "${gameConstant.getDomain(Platform.PlaytechSlot)}${path}?$urlParam"
        val headers = mapOf(
                "X-Auth-Api-Key" to clientToken.accessToken
        )
        return okHttpUtil.doGet(url = url, headers = headers, clz = PlaytechValue.Result::class.java)

    }

    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as PlaytechSlotClientToken

        val data = """
            {
                "name": "${registerReq.name}",
                "username": "${registerReq.username}",
                "password": "${registerReq.password}",
                "kiosk_name": "${clientToken.agentName}"
            }
        """.trimIndent()

        this.startPostJson(clientToken = clientToken, path = "/backoffice/player/create", data = data)

        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as PlaytechSlotClientToken

        val data = listOf(
                "player_name=${clientToken.prefix}_${balanceReq.username}",
                "server_name=${clientToken.serverName}"
        )
        val result = this.startGetJson(clientToken = clientToken, path = "/backoffice/player/serverBalance", data = data)
        check(result.code == 0) { OnePieceExceptionCode.PLATFORM_DATA_FAIL }
        val mapUtil = result.mapUtil

        val wallet = if (activeConfig.profile == "dev") "FOURBLESSINGS88" else "main_wallet"
        return mapUtil.asMap("data").asMap("wallets").asBigDecimal(wallet)
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {
        val clientToken = transferReq.token as PlaytechSlotClientToken

        val toPlayer = "${clientToken.prefix}_${transferReq.username}".toUpperCase()
        val result = when (transferReq.amount.toDouble() > 0) {
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

                this.startPostJson(clientToken = clientToken, path = "/backoffice/transfer/player/deposit", data = data)
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

                this.startPostJson(clientToken = clientToken, path = "/backoffice/transfer/player/withdraw", data = data)
            }
        }
        check(result.code == 200) { OnePieceExceptionCode.PLATFORM_DATA_FAIL }

        val platformOrderId = result.mapUtil.asMap("data").asString("reference_no")
        return GameValue.TransferResp.successful(platformOrderId = platformOrderId)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        val clientToken = checkTransferReq.token as PlaytechSlotClientToken

        val data = listOf(
                "reference_no=${checkTransferReq.platformOrderId}",
                "client_reference_no=${checkTransferReq.orderId}"
        )
        val result = this.startGetJson(clientToken = clientToken, path = "/backoffice/transfer/player/status", data = data)
        val successful = result.code == 200
        return GameValue.TransferResp.of(successful)
    }

    override fun start(startReq: GameValue.StartReq): String {
        val clientToken = startReq.token as PlaytechSlotClientToken

        error("")
    }
}
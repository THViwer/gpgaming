package com.onepiece.gpgaming.games.live

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.U9RequestStatus
import com.onepiece.gpgaming.beans.model.token.DreamGamingClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.BetOrderUtil
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class DreamGamingService : PlatformService() {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val log = LoggerFactory.getLogger(DreamGamingService::class.java)

    fun doPost(clientToken: DreamGamingClientToken, method: String, data: String): OKResponse {

        val url = "${clientToken.apiPath}$method"


        val okParam = OKParam.ofPost(url = url, param = data)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)

        if (!okResponse.ok) return okResponse

        val status = try {
            when (okResponse.asInt("codeId")) {
                0 -> U9RequestStatus.OK
                else -> U9RequestStatus.Fail
            }
        } catch (e: Exception) {
            U9RequestStatus.Fail
        }

        return okResponse.copy(status = status)
    }

    private fun getToken(clientToken: DreamGamingClientToken): Pair<String, String> {
        val random = UUID.randomUUID().toString()
        val sign = DigestUtils.md5Hex("${clientToken.agentName}${clientToken.key}$random")
        return random to sign
    }


    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val clientToken = registerReq.token as DreamGamingClientToken

        val md5Password = DigestUtils.md5Hex(registerReq.password)
        val (random, sign) = this.getToken(clientToken)
        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "data":"${clientToken.data}",
                "member":{
                    "username":"${registerReq.username}",
                    "password":"$md5Password",
                    "currencyName":"${clientToken.currency}",
                    "winLimit":0
                }
            }
        """.trimIndent()

        val okResponse = this.doPost(clientToken = clientToken, method = "/user/signup/${clientToken.agentName}", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            registerReq.username
        }
    }

    override fun updatePassword(updatePasswordReq: GameValue.UpdatePasswordReq): GameResponse<Unit> {
        val clientToken = updatePasswordReq.token as DreamGamingClientToken

        val md5Password = DigestUtils.md5Hex(updatePasswordReq.password)
        val (random, sign) = this.getToken(clientToken)
        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "member":{
                    "username":"${updatePasswordReq.username}",
                    "password":"$md5Password",
                    "status": 1,
                    "winLimit":0
                }
            }
        """.trimIndent()

        val okResponse = this.doPost(clientToken = clientToken, method = "/user/update/${clientToken.agentName}", data = data)
        return this.bindGameResponse(okResponse = okResponse) { }
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as DreamGamingClientToken
        val (random, sign) = this.getToken(clientToken)

        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "member":{"username":"${balanceReq.username}"}
            } 
        """.trimIndent()

        val okResponse = this.doPost(clientToken = clientToken, method = "/user/getBalance/${clientToken.agentName}", data = data)
        return this.bindGameResponse(okResponse = okResponse) { mapUtil ->
            mapUtil.asMap("member").asBigDecimal("balance")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = transferReq.token as DreamGamingClientToken
        val (random, sign) = this.getToken(clientToken)

        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "data":"${transferReq.orderId}",
                "member":{
                    "username":"${transferReq.username}",
                    "amount":${transferReq.amount}
                }
            } 
        """.trimIndent()

        val okResponse = this.doPost(clientToken = clientToken, method = "/account/transfer/${clientToken.agentName}", data = data)
        return this.bindGameResponse(okResponse = okResponse) { mapUtil ->
            val platformOrderId = mapUtil.asString("data")
            val balance = mapUtil.asMap("member").asBigDecimal("balance")
            GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = checkTransferReq.token as DreamGamingClientToken
        val (random, sign) = this.getToken(clientToken)

        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "data":"${checkTransferReq.orderId}"
            } 
        """.trimIndent()

        val okResponse = this.doPost(clientToken = clientToken, method = "/account/checkTransfer/${clientToken.agentName}", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            GameValue.TransferResp.of(true)
        }
    }


    override fun start(startReq: GameValue.StartReq): GameResponse<String> {
        val clientToken = startReq.token as DreamGamingClientToken

        val lang = when (startReq.language) {
            Language.EN -> "en"
            Language.CN -> "cn"
            Language.TH -> "th"
            else -> "en"
        }

        val (random, sign) = this.getToken(clientToken)
        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "lang":"$lang",
                "member":{
                    "username":"${startReq.username}"
                }
            }
        """.trimIndent()
        val okResponse = this.doPost(clientToken = clientToken, method = "/user/login/${clientToken.agentName}", data = data)
        return this.bindGameResponse(okResponse = okResponse) { mapUtil ->
            val list = mapUtil.data["list"] as List<String>

            val token = mapUtil.asString("token")
            when (startReq.launch) {
                LaunchMethod.Web -> list[0]
                LaunchMethod.Wap -> list[1]
                else -> list[2]
            }.plus(token)
        }

    }


    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {
        val clientToken = pullBetOrderReq.token as DreamGamingClientToken
        val (random, sign) = this.getToken(clientToken)

        val data = """
            {
                "token":"$sign",
                "random":"$random"
            } 
        """.trimIndent()

        val okResponse = this.doPost(clientToken = clientToken, method = "/game/getReport/${clientToken.agentName}", data = data)
        return this.bindGameResponse(okResponse = okResponse) { mapUtil ->
            val orders = mapUtil.asList("list").map { bet ->
                BetOrderUtil.instance(platform = Platform.DreamGaming, mapUtil = bet)
                        .setOrderId("id")
                        .setUsername("userName")
                        .setBetAmount("betPoints")
                        .setValidAmount("availableBet")
                        .setPayout("winOrLoss")
                        .setBetTime("betTime", dateTimeFormat)
                        .setSettleTime("calTime", dateTimeFormat)
                        .build(objectMapper)
            }

            val ids = orders.map { it.orderId.toLong() }
            this.mark(clientToken = clientToken, ids = ids)

            orders
        }
    }

    private fun mark(clientToken: DreamGamingClientToken, ids: List<Long>) {
        if (ids.isEmpty()) return

        val (random, sign) = this.getToken(clientToken)

        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "list":[${ids.joinToString(",")}]
            } 
        """.trimIndent()

        this.doPost(clientToken = clientToken, method = "/game/markReport/${clientToken.agentName}", data = data)
    }

}
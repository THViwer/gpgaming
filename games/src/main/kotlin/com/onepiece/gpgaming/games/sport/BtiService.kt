package com.onepiece.gpgaming.games.sport

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.U9RequestStatus
import com.onepiece.gpgaming.beans.model.token.BtiClientToken
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class BtiService : PlatformService() {

    fun doPostXml(path: String, data: Map<String, String>): OKResponse {

        val okParam = OKParam.ofPostXml(url = path, param = "", formParam = data)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)
        if (!okResponse.ok) return okResponse

        val errorCode = okResponse.mapUtil.asString("ErrorCode")
        if (errorCode != "NoError") return okResponse.copy(status = U9RequestStatus.Fail)

        return okResponse
    }


    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val clientToken = registerReq.token as BtiClientToken
        val data = mapOf(
                "AgentUserName" to clientToken.agentUsername,
                "AgentPassword" to clientToken.agentPassword,
                "MerchantCustomerCode" to registerReq.username,
                "LoginName" to registerReq.username,
                "CurrencyCode" to clientToken.currencyCode,
                "CountryCode" to clientToken.countryCode,

                "City" to "Kuala Lumpur",

                "FirstName" to UUID.randomUUID().toString().substring(0, 3),
                "LastName" to UUID.randomUUID().toString().substring(3, 6),
                "Group1ID" to "1",


                "CustomerMoreInfo" to "",
                "CustomerDefaultLanguage" to "en",
                "DomainID" to "",
                "DateOfBirth" to ""
        )

//        val response = this.doPostXml(path = "${clientToken.apiPath}/CreateTestUser", data = data)
        val response = this.doPostXml(path = "${clientToken.apiPath}/CreateUser", data = data)
        return this.bindGameResponse(okResponse = response) {
            registerReq.username
        }
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as BtiClientToken
        val data = mapOf(
                "AgentUserName" to clientToken.agentUsername,
                "AgentPassword" to clientToken.agentPassword,
                "MerchantCustomerCode" to balanceReq.username
        )

        val response = this.doPostXml(path = "${clientToken.apiPath}/GetBalance", data = data)
        return this.bindGameResponse(okResponse = response) {
            it.asBigDecimal("Balance")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = transferReq.token as BtiClientToken

        return if (transferReq.amount.toDouble() > 0) {
            val data = mapOf(
                    "AgentUserName" to clientToken.agentUsername,
                    "AgentPassword" to clientToken.agentPassword,
                    "MerchantCustomerCode" to transferReq.username,
                    "Amount" to "${transferReq.amount.abs()}",
                    "RefTransactionCode" to transferReq.orderId,
                    "BonusCode" to ""
            )

            val okResponse = this.doPostXml(path = "${clientToken.apiPath}/TransferToWHL", data = data)
            this.bindGameResponse(okResponse) {
                val balance = it.asBigDecimal("Balance")
                GameValue.TransferResp.of(balance = balance, platformOrderId = transferReq.orderId, successful = true)
            }

        } else {
            val data = mapOf(
                    "AgentUserName" to clientToken.agentUsername,
                    "AgentPassword" to clientToken.agentPassword,
                    "MerchantCustomerCode" to transferReq.username,
                    "Amount" to "${transferReq.amount.abs()}",
                    "RefTransactionCode" to transferReq.orderId
            )

            val okResponse = this.doPostXml(path = "${clientToken.apiPath}/TransferFromWHL", data = data)
            this.bindGameResponse(okResponse) {
                val balance = it.asBigDecimal("Balance")
                GameValue.TransferResp.successful(balance = balance, platformOrderId = transferReq.orderId)
            }
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {

        val clientToken = checkTransferReq.token as BtiClientToken

        val data = mapOf(
                "AgentUserName" to clientToken.agentUsername,
                "AgentPassword" to clientToken.agentPassword,
                "RefTransactionCode" to checkTransferReq.orderId
        )

        val okResponse = this.doPostXml(path = "${clientToken.apiPath}/CheckTransaction", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            val balance = it.asBigDecimal("Balance")
            GameValue.TransferResp.successful(balance = balance)
        }
    }

    override fun startDemo(token: ClientToken, language: Language, launch: LaunchMethod): GameResponse<String> {
        val tokenClient = token as BtiClientToken
        return GameResponse.of(tokenClient.gamePath)
    }

    override fun start(startReq: GameValue.StartReq): GameResponse<String> {
//        用亚洲版进入游戏-https：// [brand-game-domain] / [lang] / asian-view /
//        用欧洲版进入游戏-https：// [brand-game-domain] / [lang] / sports /
//        用手机版进入游戏-https：// [brand-game-domain] / [lang] / sports /
        val clientToken = startReq.token as BtiClientToken

        val data = mapOf(
                "AgentUserName" to clientToken.agentUsername,
                "AgentPassword" to clientToken.agentPassword,
                "MerchantCustomerCode" to startReq.username
        )
        val okResponse = this.doPostXml(path = "${clientToken.apiPath}/GetCustomerAuthToken", data = data)
        val authToken = this.bindGameResponse(okResponse = okResponse) {
            it.asString("AuthToken")
        }.data ?: error("Bti 未获得用户token")

        val language = when (startReq.language) {
            Language.EN -> "en"
            Language.CN -> "zh"
            Language.MY -> "ms"
            Language.VI -> "vi"
            Language.TH -> "th"
            else -> "en"
        }

        val launch = when (startReq.launch) {
            LaunchMethod.Web -> "asian-view"
            else -> "sports"
        }

        val url = "${clientToken.gamePath}/$language/${launch}/?operatorToken=$authToken"
        return GameResponse.of(url)
    }

    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd'T'HH:mm:ss")

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {

        val clientToken = pullBetOrderReq.token as BtiClientToken

        val from = pullBetOrderReq.startTime.minusHours(12).format(dateTimeFormatter)
        val to = pullBetOrderReq.endTime.minusHours(12).format(dateTimeFormatter)
        val data = """
            {
               "from":"$from",
               "to":"$to"
            }
        """.trimIndent()

        val token = this.getToken(clientToken)
        if (token.isBlank()) error("BTI token 获得异常")

        val newToken = URLEncoder.encode(token)
        val okParam = OKParam.ofPost(url = "${clientToken.orderApiPath}/dataAPI/bettinghistory?token=$newToken", param = data)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)

        return this.bindGameResponse(okResponse = okResponse) { map ->
            val list = map.asList("Bets")

            list.filter { it.asString("Status") != "Opened" }.map { order ->

                val merchantCustomerID = order.asString("MerchantCustomerID")
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.BTI, platformUsername = merchantCustomerID)
                val bet = order.asBigDecimal("TotalStake")
                val betTime = order.asLocalDateTime("CreationDate").plusHours(12) // 暂时+12
                val validBet = order.asBigDecimal("ValidStake")
                val payout = order.asBigDecimal("Return")
                val settleTime = order.asLocalDateTime("BetSettledDate")
                val orderId = order.asString("PurchaseID")

                val originData = objectMapper.writeValueAsString(order.data)
                BetOrderValue.BetOrderCo(clientId = clientId, platform = Platform.BTI, memberId = memberId, betAmount = bet, validAmount = validBet,
                    payout = payout, betTime = betTime, settleTime = settleTime, orderId = orderId, originData = originData)

            }
        }
    }

    private fun getToken(clientToken: BtiClientToken): String {

        val data = """
            { 
             "agentUserName": "${clientToken.agentUsername}", 
             "agentPassword": "${clientToken.agentPassword}" 
            }
        """.trimIndent()
        val okParam = OKParam.ofPost(url = "${clientToken.orderApiPath}/dataAPI/gettoken", param = data)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)

        return this.bindGameResponse(okResponse = okResponse) {
            it.asString("token")
        }.data ?: ""
    }
}

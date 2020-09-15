package com.onepiece.gpgaming.games.fishing

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.GGFishingClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.BetOrderUtil
import com.onepiece.gpgaming.games.bet.MapUtil
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Service
class GGFishingService : PlatformService() {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss.SSS")
    private val log = LoggerFactory.getLogger(GGFishingService::class.java)

    fun doGet(clientToken: GGFishingClientToken, path: String, data: Map<String, Any>): OKResponse {

//        val url = "${gameConstant.getDomain(Platform.GGFishing)}/api/${clientToken.webSite}/${path}"
        val url = "${clientToken.apiPath}/api/${clientToken.webSite}/${path}"

        val param = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")

        val okParam = OKParam.ofGet(url = url, param = param)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)

        if (!okResponse.ok) return okResponse

        val ok = try {
            val status = okResponse.asInt("status")
             status == 1 || status == 1003
        } catch (e: Exception) {
            false
        }
        return okResponse.copy(ok = ok)

    }


    /**
     * 0	SGD
     * 1	MYR
     * 2	HKD (1:1)
     * 3	CNY (1:1)
     * 4	JPY
     * 5	AUD
     * 6	IDR (1:1000)
     * 7	USD
     * 8	KRW
     * 9	THB
     * 11	VND (1:1000)
     * 12	NZD
     * 15	INR
     * 16	BND
     * 17	GBP
     * 18	KHR
     * 22	EUR
     * 24	RUB (1:10)
     * 25	MMKK (1:1000)
     */
    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val clientToken = registerReq.token as GGFishingClientToken
        val getKeyData = hashMapOf(
                "cert" to clientToken.cert,
                "user" to registerReq.username,
                "userName" to registerReq.name,
                "extension1" to clientToken.agentName,
                "currency" to clientToken.currency
        )
        val okResponse = this.doGet(clientToken = clientToken, path = "getKey", data = getKeyData)

        return this.bindGameResponse(okResponse) {
            registerReq.username
        }
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as GGFishingClientToken

        val data = mapOf(
                "cert" to clientToken.cert,
                "alluser" to 0,
                "users" to balanceReq.username
        )

        val okResponse = this.doGet(clientToken = clientToken, data = data, path = "getBalance")
        return this.bindGameResponse(okResponse) { mapUtil ->
            mapUtil.asList("results").first().asBigDecimal("balance")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {

        val clientToken = transferReq.token as GGFishingClientToken
        val (path, data) = if (transferReq.amount.toDouble() > 0) {
            val data = mapOf(
                    "cert" to clientToken.cert,
                    "user" to transferReq.username,
                    "balance" to transferReq.amount.abs(),
                    "ts_code" to transferReq.orderId,
                    "extension1" to clientToken.agentName
            )
            "deposit" to data
        } else {
            val data = mapOf(
                    "cert" to clientToken.cert,
                    "user" to transferReq.username,
                    "withdrawtype" to 0,
                    "balance" to transferReq.amount.abs(),
                    "ts_code" to transferReq.orderId,
                    "extension1" to clientToken.agentName
            )
            "withdraw" to data
        }

        val okResponse = this.doGet(clientToken = clientToken, path = path, data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            GameValue.TransferResp.successful()
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = checkTransferReq.token as GGFishingClientToken
        val data = mapOf(
                "cert" to clientToken.cert,
                "user" to checkTransferReq.username,
                "ts_code" to checkTransferReq.orderId
        )

        val okResponse = this.doGet(clientToken = clientToken, path = "getBalanceOperationLog", data = data)
        return this.bindGameResponse(okResponse = okResponse) { mapUtil ->
            val successful = mapUtil.asList("result").isNotEmpty()
            GameValue.TransferResp.of(successful)
        }
    }

    override fun start(startReq: GameValue.StartReq): GameResponse<String> {

        // get key
        val clientToken = startReq.token as GGFishingClientToken
        val getKeyData = hashMapOf(
                "cert" to clientToken.cert,
                "user" to startReq.username,
                "userName" to startReq.username,
                "extension1" to clientToken.agentName,
                "currency" to clientToken.currency
        )
        val okResponse = this.doGet(clientToken = clientToken, path = "/getKey", data = getKeyData)

        return this.bindGameResponse(okResponse = okResponse) { mapUtil ->
            val key = mapUtil.asString("key")

            // login
            val language = when (startReq.language) {
                Language.CN -> "cn"
                else -> "en"
            }

            val data = mapOf(
                    "user" to startReq.username,
                    "key" to URLEncoder.encode(key, "UTF-8"),
                    "extension1" to clientToken.agentName,
                    "userName" to startReq.username,
                    "fullscreen" to 1,
                    "language" to language,
                    "returnURL" to startReq.redirectUrl,
                    "gameId" to 10 // 2	水果机 3:单挑王 4:金鲨银鲨 5:幸运五张 6:大鱼吃小鱼 7:	射龙门 8:钻石迷城 9:森林舞会 10	:捕鱼2 301:世界杯 302:奔驰宝马
            )

            val param = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
            "${clientToken.apiPath}/api/${clientToken.webSite}/loginV2?$param"
        }

    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {

        val clientToken = pullBetOrderReq.token as GGFishingClientToken

        return this.pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.GGFishing) { lastupdatedate ->
            val data = mapOf(
                    "cert" to clientToken.cert,
                    "extension1" to clientToken.agentName,
                    "status" to 1,
                    "lastupdatedate" to lastupdatedate
            )

            val okResponse = this.doGet(clientToken = clientToken, path = "getTransactionsByLastUpdateDate", data = data)

            val gameResponse = this.bindGameResponse(okResponse = okResponse) { mapUtil ->
                 mapUtil.asList("transactions").map { bet ->

                    BetOrderUtil.instance(platform = Platform.GGFishing, mapUtil = bet)
                            .setOrderId("id")
                            .setUsername("userId")
                            .setBetAmount("realBetAmount")
                            .setWinAmount("realPayAmount")
                            .setBetTime("betTransTime", dateTimeFormat)
                            .setSettleTime("updateTime", dateTimeFormat)
                            .build(objectMapper)
                }
            }

            val nextId = gameResponse.data?.lastOrNull()?.let { "${it.betTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()}" } ?: lastupdatedate

            BetNextIdData(nextId = nextId, gameResponse = gameResponse)
        }

    }
}
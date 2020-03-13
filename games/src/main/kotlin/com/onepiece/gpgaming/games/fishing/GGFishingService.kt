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

    fun startDoGet(clientToken: GGFishingClientToken, path: String, data: Map<String, Any>): MapUtil {

//        val url = "${gameConstant.getDomain(Platform.GGFishing)}/api/${clientToken.webSite}/${path}"
        val url = "${clientToken.apiPath}/${clientToken.webSite}/api/${path}"

        val param = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
        val result = okHttpUtil.doGet(url = "$url?${param}", clz = GGFishingValue.Result::class.java)

        check(result.status == 1 || result.status == 1003) {
            log.error("ggFishing network error: ${result.status}")
            OnePieceExceptionCode.PLATFORM_DATA_FAIL
        }
        return result.mapUtil
    }


    override fun register(registerReq: GameValue.RegisterReq): String {

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

        // get key
        val clientToken = registerReq.token as GGFishingClientToken
        val getKeyData = hashMapOf(
                "cert" to clientToken.cert,
                "user" to registerReq.username,
                "userName" to registerReq.name,
                "extension1" to clientToken.agentName,
                "currency" to clientToken.currency
        )
        val getKeyDataMap = this.startDoGet(clientToken = clientToken, path = "getKey", data = getKeyData)
        getKeyDataMap.asString("key")

        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as GGFishingClientToken

        val data = mapOf(
                "cert" to clientToken.cert,
                "alluser" to 0,
                "users" to balanceReq.username
        )

        val mapUtil = this.startDoGet(clientToken = clientToken, data = data, path = "getBalance")
        return mapUtil.asList("results").first().asBigDecimal("balance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {

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

        this.startDoGet(clientToken = clientToken, path = path, data = data)
        return GameValue.TransferResp.successful()
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        val clientToken = checkTransferReq.token as GGFishingClientToken
        val data = mapOf(
                "cert" to clientToken.cert,
                "user" to checkTransferReq.username,
                "ts_code" to checkTransferReq.orderId
        )

        val mapUtil = this.startDoGet(clientToken = clientToken, path = "getBalanceOperationLog", data = data)

        val successful = mapUtil.asList("result").isNotEmpty()
        return GameValue.TransferResp.of(successful)
    }

    override fun start(startReq: GameValue.StartReq): String {

        // get key
        val clientToken = startReq.token as GGFishingClientToken
        val getKeyData = hashMapOf(
                "cert" to clientToken.cert,
                "user" to startReq.username,
                "userName" to startReq.username,
                "extension1" to clientToken.agentName,
                "currency" to clientToken.currency
        )
        val getKeyDataMap = this.startDoGet(clientToken = clientToken, path = "/getKey", data = getKeyData)
        val key = getKeyDataMap.asString("key")

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
        return "${clientToken.apiPath}/api/${clientToken.webSite}/loginV2?$param"
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {

        val clientToken = pullBetOrderReq.token as GGFishingClientToken

        return this.pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.GGFishing) { lastupdatedate ->
            val data = mapOf(
                    "cert" to clientToken.cert,
                    "extension1" to clientToken.agentName,
                    "status" to 1,
                    "lastupdatedate" to lastupdatedate
            )

            val mapUtil = this.startDoGet(clientToken = clientToken, path = "getTransactionsByLastUpdateDate", data = data)
            val orders = mapUtil.asList("transactions").map { bet ->

                BetOrderUtil.instance(platform = Platform.GGFishing, mapUtil = bet)
                        .setOrderId("id")
                        .setUsername("userId")
                        .setBetAmount("realBetAmount")
                        .setWinAmount("realPayAmount")
                        .setBetTime("betTransTime", dateTimeFormat)
                        .setSettleTime("updateTime", dateTimeFormat)
                        .build(objectMapper)
            }
            val nextId = orders.lastOrNull()?.let { "${it.betTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()}" } ?: lastupdatedate

            nextId to orders
        }

    }
}
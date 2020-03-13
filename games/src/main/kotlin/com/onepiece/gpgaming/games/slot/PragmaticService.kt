package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.model.token.PragmaticClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.beans.value.internet.web.SlotGame
import com.onepiece.gpgaming.core.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.MapUtil
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class PragmaticService: PlatformService() {

    private val log = LoggerFactory.getLogger(PragmaticService::class.java)

    fun startDoPostForm(method: String, clientToken: PragmaticClientToken, data: Map<String, Any>): MapUtil {

        val param = data.map { "${it.key}=${it.value}" }.sorted().joinToString("&")
        val signParam = "$param${clientToken.secret}"
        val sign = DigestUtils.md5Hex(signParam)

        val urlParam = "$param&hash=$sign"
        val url = "${clientToken.apiPath}/IntegrationService/v3/http/CasinoGameAPI${method}"

        val result = okHttpUtil.doGet(url = "$url?$urlParam", clz = PragmaticValue.Result::class.java)
        check(result.error == 0) {
            log.error("pragmatic network error: error = ${result.error}, msg = $result")

            OnePieceExceptionCode.PLATFORM_DATA_FAIL
        }

        return result.mapUtil
    }

    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as PragmaticClientToken

        val data = hashMapOf(
                "secureLogin" to clientToken.secureLogin,
                "externalPlayerId" to registerReq.username,
                "currency" to clientToken.currency
        )

        this.startDoPostForm(method = "/player/account/create", clientToken = clientToken, data = data)

        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as PragmaticClientToken

        val data = hashMapOf(
                "secureLogin" to clientToken.secureLogin,
                "externalPlayerId" to balanceReq.username
        )
        val mapUtil = this.startDoPostForm(method = "/balance/current", clientToken = clientToken, data = data)
        return mapUtil.asBigDecimal("balance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {
        val clientToken = transferReq.token as PragmaticClientToken

        val data = hashMapOf(
                "secureLogin" to clientToken.secureLogin,
                "externalPlayerId" to transferReq.username,
                "externalTransactionId" to transferReq.orderId,
                "amount" to transferReq.amount.setScale(2, 2)
        )
        val mapUtil = this.startDoPostForm(method = "/balance/transfer", clientToken = clientToken, data = data)
        val balance = mapUtil.asBigDecimal("balance")
        return GameValue.TransferResp.successful(balance = balance)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        val clientToken = checkTransferReq.token as PragmaticClientToken

        val data = hashMapOf(
                "secureLogin" to clientToken.secureLogin,
                "externalTransactionId" to checkTransferReq.orderId
        )
        val mapUtil = this.startDoPostForm(method = "/balance/transfer/status/", clientToken = clientToken, data = data)
        val successful = mapUtil.asString("status") == "Success"
        val balance = mapUtil.asBigDecimal("balance")
        return GameValue.TransferResp.of(successful = successful, balance = balance)
    }


    override fun slotGames(token: ClientToken, launch: LaunchMethod, language: Language): List<SlotGame> {
        val clientToken = token as PragmaticClientToken

        val data = hashMapOf(
                "secureLogin" to clientToken.secureLogin
        )
        val mapUtil = this.startDoPostForm(method = "/getCasinoGames", clientToken = clientToken, data = data)

        return mapUtil.asList("gameList").filter {
            // MOBILE,DOWNLOAD,WEB
            when (launch) {
                LaunchMethod.Wap -> it.asString("platform").contains("MOBILE")
                else -> it.asString("platform").contains("WEB")
            }
        }.map { bet ->

            val gameId = bet.asString("gameID")
            val gameName = bet.asString("gameName")

            val typeDescription = bet.asString("typeDescription")
//            val gameCategory = when (typeDescription) {
//                "Video Slots" -> GameCategory.SlotVideo
//                "Classic Slots" -> GameCategory.SLOT
//                "Blackjack" -> GameCategory.Blackjack
//                "Scratch card" -> GameCategory.ScratchCard
//                "Baccarat New" -> GameCategory.BaccaratNew
//                "Baccarat" -> GameCategory.Baccarat
//                "Keno" -> GameCategory.Keno
//                "Roulette" -> GameCategory.Roulette
//                "Video Poker" -> GameCategory.VideoPoker
//                else -> GameCategory.Default
//            }

            // 启动方式
            val technology = "html5"

            /**
             * 矩形，大小 325x234：
             * http(s)://{game server domain}/game_pic/rec/325/{gameID}.png
             * 矩形，大小 188x83：
             * http(s)://{game server domain}/game_pic/rec/188/{gameID}.png
             * 矩形，大小 160x115：
             * http(s)://{game server domain}/game_pic/rec/160/{gameID}.png
             * 方形，大小 200x200：
             * http(s)://{game server domain}/game_pic/square/200/{gameID}.png
             * 方形，大小 138x138：
             * http(s)://{game server domain}/game_pic/square/138/{gameID}.jpg
             */
            val icon = "${gameConstant.getDomain(Platform.Pragmatic)}/game_pic/rec/325/${gameId}.png"

            SlotGame(gameId = gameId, gameName = gameName, category = GameCategory.Slot, icon = icon, touchIcon = null, hot = false, new = false,
                    status = Status.Normal, platform = Platform.Pragmatic)
        }
    }


    override fun startSlotDemo(startSlotReq: GameValue.StartSlotReq): String {
        val lang = when (startSlotReq.language) {
            // de, en, es, fr, it, ja, ru, th, tr, vi, zh
            Language.CN -> "zh"
            Language.VI -> "vi"
            Language.TH -> "th"
            Language.EN -> "en"
            Language.MY -> "ms"
            else -> "en"
        }

        return "http://demogamesfree.pragmaticplay.net/gs2c/openGame.do?gameSymbol=${startSlotReq.gameId}&lang=$lang&cur=MYR&lobbyUrl=${startSlotReq.redirectUrl}"
    }



    override fun startSlot(startSlotReq: GameValue.StartSlotReq): String {

        val clientToken = startSlotReq.token as PragmaticClientToken

        val language = when (startSlotReq.language) {
            // de, en, es, fr, it, ja, ru, th, tr, vi, zh
            Language.CN -> "zh"
            Language.VI -> "vi"
            Language.TH -> "th"
            Language.EN -> "en"
            Language.MY -> "ms"
            else -> "en"
        }
        val platform = when (startSlotReq.launchMethod) {
            LaunchMethod.Web -> "WEB"
            LaunchMethod.Wap -> "MOBILE"
            else -> "WEB"
        }
        val data = mapOf(
                "secureLogin" to clientToken.secureLogin,
                "externalPlayerId" to startSlotReq.username,
                "gameId" to startSlotReq.gameId,
                "language" to language,
                "platform" to platform,
//                "cashierURL" to "" // 当玩家资金不足时用于打开娱乐场运营商站点上的收银台的链接
                "lobbyURL" to startSlotReq.redirectUrl
        )

        val mapUtil = this.startDoPostForm(method = "/game/start", data = data, clientToken = clientToken)
        return mapUtil.asString("gameURL")
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val clientToken = pullBetOrderReq.token as PragmaticClientToken

        val timepoint = System.currentTimeMillis()
        return this.pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.Pragmatic) { startId: String ->
            val urlParam = listOf(
                    "login=${clientToken.secureLogin}",
                    "password=${clientToken.secret}",
                    "timepoint=$startId"
            ).joinToString(separator = "&")

//            val url = "${gameConstant.getDomain(Platform.Pragmatic)}/IntegrationService/v3/DataFeeds/transactions?$urlParam"
            val url = "${gameConstant.getDomain(Platform.Pragmatic)}/IntegrationService/v3/DataFeeds/gamerounds/finished/?$urlParam"
            val csv = okHttpUtil.doGet(url = url, clz = String::class.java)
            val orders = parseCsv(csv = csv)
            "$timepoint" to orders
        }

    }

    private fun parseCsv(csv: String): List<BetOrderValue.BetOrderCo> {
        val list = arrayListOf<MapUtil>()
        csv.lines().forEachIndexed { index, s ->
            when (index) {
                0, 1 -> {}
                else -> {
                    if (s.isBlank()) return@forEachIndexed
                    val data = s.split(",")

                    // playerID,extPlayerID,gameID,playSessionID,timestamp,referenceID,type,amount,currency
                    val map = hashMapOf(
                            "playerID" to data[0],
                            "extPlayerID" to data[1],
                            "gameID" to data[2],
                            "playSessionID" to data[3],
                            "timestamp" to data[4],
                            "referenceID" to data[5],
                            "type" to data[6],
                            "amount" to data[7],
                            "currency" to data[8]
                    )

                    if (map["type"] == "B") {
                        map["betAmount"] = map["amount"]!!
                        map["winAmount"] = "0"
                    } else {
                        map["betAmount"] = "0"
                        map["winAmount"] = map["amount"]!!
                    }

                    val mapUtil = MapUtil.instance(map)
                    list.add(mapUtil)
                }
            }
        }


        return list.map { bet ->

            val username = bet.asString("extPlayerID")
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(Platform.Pragmatic, username)
            val orderId = bet.asString("referenceID")
            val betTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(bet.asLong("timestamp")), ZoneId.of("Asia/Shanghai"))
            val betAmount = bet.asBigDecimal("betAmount")
            val winAmount = bet.asBigDecimal("winAmount")

            val originData = objectMapper.writeValueAsString(bet.data)
            BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, betAmount = betAmount, winAmount = winAmount, platform = Platform.Pragmatic,
                    betTime = betTime, settleTime = betTime, orderId = orderId, originData = originData)
        }

    }

}

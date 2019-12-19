package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.model.token.SpadeGamingClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.beans.value.internet.web.SlotGame
import com.onepiece.gpgaming.core.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.MapUtil
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class SpadeGamingService : PlatformService() {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")

    private fun startPostJson(method: String, data: String): MapUtil {

        val url = "${gameConstant.getDomain(Platform.SpadeGaming)}/api"
        val headers = mapOf(
                "API" to method,
                "DataType" to "JSON"
        )
        val result = okHttpUtil.doPostJson(url = url, data = data, headers = headers, clz = SpadeGamingValue.Result::class.java)
        check(result.code == 0)

        return result.mapUtil
    }

    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as SpadeGamingClientToken

        val data = """
            {
                "acctId": "${registerReq.username}",
                "userName": "${registerReq.name}",
                "currency": "${clientToken.currency}",
                "siteId": "${clientToken.siteId}",
                "merchantCode": "${clientToken.memberCode}",
                "serialNo": "${UUID.randomUUID()}"
            }
        """.trimIndent()

        this.startPostJson(method = "createAcct", data = data)
        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as SpadeGamingClientToken

        val data = """
            {
                "acctId": "${balanceReq.username}",
                "pageIndex":0,
                "merchantCode": "${clientToken.memberCode}",
                "serialNo": "${UUID.randomUUID()}" 
            }
        """.trimIndent()
        val mapUtil = this.startPostJson(method = "getAcctInfo", data = data)
        return mapUtil.asList("list").first().asBigDecimal("balance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {
        val clientToken = transferReq.token as SpadeGamingClientToken

        val data = """
                    {
                        "acctId": "${transferReq.username}",
                        "amount": ${transferReq.amount.abs()},
                        "currency": "${clientToken.currency}",
                        "merchantCode": "${clientToken.memberCode}",
                        "serialNo": "${transferReq.orderId}"
                    }
                """.trimIndent()

        val method = if (transferReq.amount.toDouble() > 0) "deposit" else "withdraw"
        val mapUtil = this.startPostJson(method = method, data = data)
        val platformOrderId = mapUtil.asString("serialNo")
        val balance = mapUtil.asBigDecimal("afterBalance")
        return GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        val clientToken = checkTransferReq.token as SpadeGamingClientToken

        val startTime = LocalDate.now().atStartOfDay()
        val endTime = startTime.plusDays(1)
        val data = """
            {
                "beginDate":"${startTime.format(dateTimeFormat)}",
                "endDate":"${endTime.format(dateTimeFormat)}",
                "acctId":"${checkTransferReq.username}",
                "currency":"${clientToken.currency}",
                "lastSerialNo":"${checkTransferReq.orderId}",
                "serialNo":"${UUID.randomUUID()}",
                "pageIndex":1,
                "merchantCode":"${clientToken.memberCode}"
            }
        """.trimIndent()

        val mapUtil = this.startPostJson(method = "fundInOut", data = data)
        val successful = mapUtil.asInt("resultCount") == 1
        return GameValue.TransferResp.of(successful)
    }

    override fun slotGames(token: ClientToken, launch: LaunchMethod, language: Language): List<SlotGame> {

        val clientToken = token as SpadeGamingClientToken

        val data = """
            {
                "merchantCode": "${clientToken.memberCode}",
                "serialNo": "${UUID.randomUUID()}"
            }
            
        """.trimIndent()

        val mapUtil = this.startPostJson(method = "getGames", data = data)

        return mapUtil.asList("games").map { game ->

            /**
             * gameCode Varchar(10) 是 S-GD02 游戏代码
            gameName Varchar(30) 是 DerbyNight 游戏名称
            jackpot boolean 是 True 是否有 jackpot
            thumbnail Varchar(100) 是 /images/aa.jpg 游戏图片
            screenshot Varchar(100) 是 /images/bb.jpg 游戏快照
            mthumbnail Varchar(100) 是 /images/cc.jpg 手机游戏图片
            jackpotCode Varchar(50) 否 Holy 只 jackpot 游戏
            jackpotName Varchar(50) 否 Holy Jackpot 只 jackpot 游戏
             */
            val gameId = game.asString("gameCode")
            val gameName = game.asString("gameName")
            val icon = game.asString("thumbnail")

            SlotGame(gameId = gameId, gameName = gameName, category = GameCategory.Slot, icon = icon, touchIcon = null, hot = false,
                    new = false, status = Status.Normal, platform = Platform.SpadeGaming)
        }
    }

    private fun getToken(startSlotReq: GameValue.StartSlotReq): String {
        val clientToken = startSlotReq.token as SpadeGamingClientToken
        val data = """
            {
                "acctId":"${startSlotReq.username}",
                "merchantCode":"${clientToken.memberCode}",
                "action":"ticketLog",
                "serialNo": "${UUID.randomUUID()}"
            }
        """.trimIndent()

        val mapUtil = this.startPostJson(method = "createToken", data = data)
        return mapUtil.asString("token")
    }


    override fun startSlotDemo(startSlotReq: GameValue.StartSlotReq): String {

//        val clientToken = startSlotReq.token as SpadeGamingClientToken
//        val token = this.getToken(startSlotReq)
//
//        val mobile = startSlotReq.launchMethod == LaunchMethod.Wap
//        val urlParam = listOf(
//                "acctId=${StringUtil.generateNonce(6)}",
//                "language=en",
//                "token=$token",
//                "game=${startSlotReq.gameId}",
//                "fun=true",
////                "minigame=false",
//                "mobile=$mobile",
//                "menumode=on"
//        ).joinToString(separator = "&")
//
//        return "http://lobby-egame-staging.sgplay.net/${clientToken.memberCode}/auth?$urlParam"

        val lang = when (startSlotReq.language) {
            Language.EN -> "en-US"
            Language.TH -> "th_TH"
            Language.ID -> "id_ID"
            Language.VI -> "vi_VN"
            Language.CN -> "zh_CN"
            else -> "en-US"
        }
        val type = if (startSlotReq.launchMethod == LaunchMethod.Web) "web"  else "mobile"

        return "http://lobby.sgplayfun.com/index.jsp?game=${startSlotReq.gameId}&language=${lang}&type=$type"
    }

    override fun startSlot(startSlotReq: GameValue.StartSlotReq): String {
        val clientToken = startSlotReq.token as SpadeGamingClientToken
        val token = this.getToken(startSlotReq)

        /**
         * en_US 英文
        zh_CN 简体中文
        th_TH 泰文
        id_ID 印尼文
        vi_VN 越南文
        ko_KR 韩文 ja_JP 日文
         */

        val lang = when (startSlotReq.language) {
            Language.EN -> "en-US"
            Language.TH -> "th_TH"
            Language.ID -> "id_ID"
            Language.VI -> "vi_VN"
            Language.CN -> "zh_CN"
            else -> "en-US"
        }

        val mobile = startSlotReq.launchMethod == LaunchMethod.Wap
        val urlParam = listOf(
                "acctId=${startSlotReq.username}",
                "language=${lang}",
                "token=$token",
                "game=${startSlotReq.gameId}",
//                "fun=false",
//                "minigame=false",
                "mobile=$mobile",
                "menumode=on"
        ).joinToString(separator = "&")
//        return "http://portal.e-games.com/auth/?$urlParam"
//        return "http://lobby-egame-staging.sgplay.net/${clientToken.memberCode}/auth/?$urlParam"

        // http://portal.e-games.com/auth/?acctId=TESTPLAYER1&language=en_US&token=fe1a85adc54545d2963b661a22d09c9e&game=S-DG02&fun=true&minigame=false&mobile=true&menumode=on

        return "http://lobby-egame-staging.sgplay.net/${clientToken.memberCode}/auth?$urlParam"
    }


    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        var index = 1
        var count = 1
        val orders = arrayListOf<BetOrderValue.BetOrderCo>()
        do {
            val (pageCount, list) = this.pullBetOrders(pullBetOrderReq, index)
            orders.addAll(list)
            index ++
            count = pageCount
        } while (index <= count)
        return orders
    }

    private fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq, pageIndex: Int): Pair<Int, List<BetOrderValue.BetOrderCo>> {
        val clientToken = pullBetOrderReq.token as SpadeGamingClientToken


        val data = """
            {
                "beginDate": "${pullBetOrderReq.startTime.format(dateTimeFormat)}",
                "endDate": "${pullBetOrderReq.endTime.format(dateTimeFormat)}",
                "pageIndex": ${pageIndex},
                "merchantCode": "${clientToken.memberCode}",
                "serialNo": "${UUID.randomUUID()}"
            }
        """.trimIndent()

        val mapUtil = this.startPostJson(method = "getBetHistory", data = data)
        val orders = mapUtil.asList("list").filter { it.asBoolean("completed") }.map { bet ->

            val orderId = bet.asString("ticketId")
            val username = bet.asString("acctId")
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.SpadeGaming, platformUsername = username)
            val betAmount = bet.asBigDecimal("betAmount")
            val winLoss = bet.asBigDecimal("winLoss")
            val winAmount = betAmount.plus(winLoss)

            val betTime = bet.asLocalDateTime("ticketTime", dateTimeFormat)

            val originData = objectMapper.writeValueAsString(bet.data)
            BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, platform = Platform.SpadeGaming, betTime = betTime,
                    settleTime = betTime, betAmount = betAmount, winAmount = winAmount, originData = originData)
        }
        val pageCount =  mapUtil.asInt("pageCount")
        return pageCount to orders
    }

}


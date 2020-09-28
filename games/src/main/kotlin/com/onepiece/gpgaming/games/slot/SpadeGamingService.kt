package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.U9RequestStatus
import com.onepiece.gpgaming.beans.model.token.SpadeGamingClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.ActiveConfig
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class SpadeGamingService(
        private val activeConfig: ActiveConfig
) : PlatformService() {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
    private val log = LoggerFactory.getLogger(SpadeGamingService::class.java)

    private fun doPost(clientToken: SpadeGamingClientToken, method: String, data: String): OKResponse {

        val url = "${clientToken.apiPath}/api"
        val headers = mapOf(
                "API" to method,
                "DataType" to "JSON"
        )

        val okParam = OKParam.ofPost(url = url, param = data, headers = headers)
        val okResponse = u9HttpRequest.startRequest(okParam)
        if (!okResponse.ok) return okResponse

        val status = try {
            when (okResponse.asInt("code")) {
                0 -> U9RequestStatus.OK
                else -> U9RequestStatus.Fail
            }
        } catch (e: Exception) {
            U9RequestStatus.Fail
        }
        return okResponse.copy(status = status)
    }

    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        fun newRegister(amount: BigDecimal) {
            val orderId = UUID.randomUUID().toString().replace("-", "")
            val transferReq = GameValue.TransferReq(token = registerReq.token, orderId = orderId, amount = amount,
                    password = registerReq.password, username = registerReq.username)
            this.transfer(transferReq)
        }

        // deposit
        newRegister(BigDecimal.valueOf(0.01))

        // withdraw
        newRegister(BigDecimal.valueOf(-0.01))

        return GameResponse.of(data = registerReq.username)


    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as SpadeGamingClientToken

        val data = """
            {
                "acctId": "${balanceReq.username}",
                "pageIndex":0,
                "merchantCode": "${clientToken.memberCode}",
                "serialNo": "${UUID.randomUUID()}" 
            }
        """.trimIndent()
        val okResponse = this.doPost(clientToken = clientToken, method = "getAcctInfo", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asList("list").first().asBigDecimal("balance")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
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
        val okResponse = this.doPost(clientToken = clientToken, method = method, data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            val platformOrderId = it.asString("serialNo")
            val balance = it.asBigDecimal("afterBalance")
            GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
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

        val okResponse = this.doPost(clientToken = clientToken, method = "fundInOut", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            val successful = it.asInt("resultCount") == 1
            GameValue.TransferResp.of(successful)
        }
    }

//    override fun slotGames(token: ClientToken, launch: LaunchMethod, language: Language): List<SlotGame> {
//
//        val clientToken = token as SpadeGamingClientToken
//
//        val data = """
//            {
//                "merchantCode": "${clientToken.memberCode}",
//                "serialNo": "${UUID.randomUUID()}"
//            }
//
//        """.trimIndent()
//
//        val mapUtil = this.startPostJson(clientToken = clientToken, method = "getGames", data = data)
//
//        return mapUtil.asList("games").map { game ->
//
//            /**
//             * gameCode Varchar(10) 是 S-GD02 游戏代码
//            gameName Varchar(30) 是 DerbyNight 游戏名称
//            jackpot boolean 是 True 是否有 jackpot
//            thumbnail Varchar(100) 是 /images/aa.jpg 游戏图片
//            screenshot Varchar(100) 是 /images/bb.jpg 游戏快照
//            mthumbnail Varchar(100) 是 /images/cc.jpg 手机游戏图片
//            jackpotCode Varchar(50) 否 Holy 只 jackpot 游戏
//            jackpotName Varchar(50) 否 Holy Jackpot 只 jackpot 游戏
//             */
//            val gameId = game.asString("gameCode")
//            val gameName = game.asString("gameName")
//            val icon = game.asString("thumbnail")
//
//            SlotGame(gameId = gameId, gameName = gameName, category = GameCategory.Slot, icon = icon, touchIcon = null, hot = false,
//                    new = false, status = Status.Normal, platform = Platform.SpadeGaming)
//        }
//    }

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

        val okResponse = this.doPost(clientToken = clientToken, method = "createToken", data = data)
        return okResponse.asString("token")
    }


    override fun startSlotDemo(startSlotReq: GameValue.StartSlotReq): GameResponse<String> {

        val lang = when (startSlotReq.language) {
            Language.EN -> "en-US"
            Language.TH -> "th_TH"
            Language.ID -> "id_ID"
            Language.VI -> "vi_VN"
            Language.CN -> "zh_CN"
            else -> "en-US"
        }
        val type = if (startSlotReq.launchMethod == LaunchMethod.Web) "web" else "mobile"

        val path = "http://lobby.sgplayfun.com/index.jsp?game=${startSlotReq.gameId}&language=${lang}&type=$type"
        return GameResponse.of(data = path)
    }

    override fun startSlot(startSlotReq: GameValue.StartSlotReq): GameResponse<String> {
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

        val path = if (activeConfig.profile == "prod") {
            "http://lobby.silverkirinplay.com/${clientToken.memberCode}/auth?$urlParam"
        } else {
            "http://lobby-egame-staging.sgplay.net/${clientToken.memberCode}/auth?$urlParam"
        }
        return GameResponse.of(data = path)
    }


    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {
        var index = 1
        var count = 1
        val orders = arrayListOf<BetOrderValue.BetOrderCo>()

        var rGameResponse = GameResponse.of(data = emptyList<BetOrderValue.BetOrderCo>())

        do {
            val (pageCount, gameResponse) = this.pullBetOrders(pullBetOrderReq, index)
            if (!gameResponse.okResponse.ok) return gameResponse

            rGameResponse = gameResponse

            orders.addAll(gameResponse.data!!)
            index++
            count = pageCount
        } while (index <= count)

        return rGameResponse.copy(data = orders)
    }

    private fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq, pageIndex: Int): Pair<Int, GameResponse<List<BetOrderValue.BetOrderCo>>> {
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

        val okResponse = this.doPost(clientToken = clientToken, method = "getBetHistory", data = data)

        val pageCount = try {
            okResponse.asInt("pageCount")
        } catch (e: Exception) {
            0
        }

        return pageCount to this.bindGameResponse(okResponse = okResponse) { mapUtil ->
            mapUtil.asList("list").filter { it.asBoolean("completed") }.map { bet ->

                val orderId = bet.asString("ticketId")
                val username = bet.asString("acctId")
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.SpadeGaming, platformUsername = username)
                val betAmount = bet.asBigDecimal("betAmount")
                val winLoss = bet.asBigDecimal("winLoss")
                val winAmount = betAmount.plus(winLoss)

                val betTime = bet.asLocalDateTime("ticketTime", dateTimeFormat)

                val originData = objectMapper.writeValueAsString(bet.data)
                BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, platform = Platform.SpadeGaming, betTime = betTime,
                        settleTime = betTime, betAmount = betAmount, winAmount = winAmount, originData = originData, validAmount = betAmount)
            }
        }

    }

}


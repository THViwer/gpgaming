package com.onepiece.treasure.games.live

import com.onepiece.treasure.beans.enums.GameCategory
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.token.ClientToken
import com.onepiece.treasure.beans.model.token.SpadeGamingClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.BetOrderUtil
import com.onepiece.treasure.games.bet.MapUtil
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class SpadeGamingService : PlatformService() {

    private val currency = "MYR"
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
                "currency": "$currency",
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

    override fun transfer(transferReq: GameValue.TransferReq): String {
        val clientToken = transferReq.token as SpadeGamingClientToken


        val data = """
                    {
                        "acctId": "${transferReq.username}",
                        "amount": ${transferReq.amount},
                        "currency": "$currency",
                        "merchantCode": "${clientToken.memberCode}",
                        "serialNo": "${UUID.randomUUID()}"
                    }
                """.trimIndent()

        val method = if (transferReq.amount.toDouble() > 0) "deposit" else "withdraw"
        val result = this.startPostJson(method = method, data = data)
        return result.asString("serialNo")
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val clientToken = checkTransferReq.token as SpadeGamingClientToken

        val startTime = LocalDate.now().atStartOfDay()
        val endTime = startTime.plusDays(1)
        val data = """
            {
                "beginDate":"${startTime.format(dateTimeFormat)}",
                "endDate":"${endTime.format(dateTimeFormat)}",
                "acctId":"${checkTransferReq.username}",
                "currency":"$currency",
                "lastSerialNo":”${checkTransferReq.orderId}”
                "serialNo":"${UUID.randomUUID()}"
                "pageIndex":0,
                "merchantCode":"${clientToken.memberCode}",
            }
        """.trimIndent()

        val mapUtil = this.startPostJson(method = "fundInOut", data = data)
        return mapUtil.asInt("resultCount") == 1
    }

    //TODO 需要把游戏保存到db中 图片上传到aws s3上 因为公网请求不了图片
    override fun slotGames(token: ClientToken, launch: LaunchMethod): List<SlotGame> {

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

            SlotGame(gameId = gameId, gameName = gameName, category = GameCategory.Default, icon = icon, touchIcon = null, hot = false,
                    new = false, status = Status.Normal, chineseGameName = gameName)
        }
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

        val mapUtil = this.startPostJson(method = "", data = data)
        val orders = mapUtil.asList("list").filter { it.asBoolean("completed") }.map { bet ->

            BetOrderUtil.instance(platform = Platform.SpadeGaming, mapUtil = bet)
                    .setOrderId("ticketId")
                    .setUsername("acctId")
                    .setBetAmount("betAmount")
                    .setWinAmount("winLoss")
                    .setBetTime("ticketTime", dateTimeFormat)
                    .setSettleTime("ticketTime", dateTimeFormat)
                    .build(objectMapper)
        }
        val pageCount =  mapUtil.asInt("pageCount")
        return pageCount to orders
    }

}

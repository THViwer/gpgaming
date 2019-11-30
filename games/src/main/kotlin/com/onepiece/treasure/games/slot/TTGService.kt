package com.onepiece.treasure.games.slot

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.ClientToken
import com.onepiece.treasure.beans.model.token.TTGClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.MapUtil
import okhttp3.MediaType.Companion.toMediaType
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("UNCHECKED_CAST")
@Service
class TTGService: PlatformService() {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")

    private fun startPostXml(method: String, data: String): MapUtil {

        val url = "${gameConstant.getDomain(Platform.TTG)}${method}"
        val xmlData = okHttpUtil.doPostXml(url = url, data = data, clz = Map::class.java)
        return MapUtil.instance(data = xmlData as Map<String, Any>)
    }



    override fun register(registerReq: GameValue.RegisterReq): String {

        val tokenClient = registerReq.token as TTGClientToken
        this.login(username = registerReq.username, tokenClient = tokenClient)

        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val url = "${gameConstant.getDomain(Platform.TTG)}/cip/player/${balanceReq.username}/balance"
        val xml = okHttpUtil.doGetXml(url = url, clz = Map::class.java)
        val mapUtil = MapUtil.instance(xml as Map<String, Any>)
        return mapUtil.asBigDecimal("real")
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

        val tokenClient = transferReq.token as TTGClientToken

        val data = """
            <transactiondetail uid="${transferReq.username}" amount="${transferReq.amount}" />
        """.trimIndent()
        val mapUtil = this.startPostXml(method = "/cip/transaction/${tokenClient.agentName}/${transferReq.orderId}", data = data)
        check(mapUtil.asString("retry") == "0") { OnePieceExceptionCode.PLATFORM_DATA_FAIL }

        return transferReq.orderId
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val tokenClient = checkTransferReq.token as TTGClientToken
        val url = "${gameConstant.getDomain(Platform.TTG)}/cip/transaction/${tokenClient.agentName}/${checkTransferReq.orderId}"
        val xml = okHttpUtil.doGetXml(url = url, clz = String::class.java)
        val mapUtil = MapUtil.instance(xml as Map<String, Any>)
        return mapUtil.data["uid"] != null
    }

    private fun login(username: String, tokenClient: TTGClientToken): MapUtil {
        val data = """
            <?xml version="1.0" encoding="UTF-8"?>
            <logindetail>
               <player account="MYR" country="MY" firstName="" lastName="" userName="" nickName="" tester="0" partnerId="Gpgaming88" commonWallet="0" />
               <partners>
                  <partner partnerId="zero" partnerType="0" />
                  <partner partnerId="IG" partnerType="1" />
                  <partner partnerId="${tokenClient.agentName}" partnerType="1" />
               </partners>
            </logindetail>
        """.trimIndent()
        return this.startPostXml(method = "/cip/gametoken/${username}", data = data)
    }

    override fun slotGames(token: ClientToken, launch: LaunchMethod): List<SlotGame> {
        return when (launch) {
            LaunchMethod.Wap -> TTGGames.mobileGames
            LaunchMethod.Web -> TTGGames.pcGames
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    override fun startSlotDemo(startSlotReq: GameValue.StartSlotReq): String {
        val tokenClient = startSlotReq.token as TTGClientToken
        /**
         * Language = Simplified Chinese (zh-cn)
         * Language = Traditional Chinese (zh-tw)
         * Language = Vietnamese (vi)
         * Language = Korean (ko)
         * Language = Japanese (ja)
         * Language = Thai (th)
         * Language = English (en)
         */
        val lang = when (startSlotReq.language) {
            Language.CN -> "zh-cn"
            Language.VI -> "vi"
            Language.TH -> "th"
            Language.EN -> "en"
            else -> "en"
        }

        val (gameId, gameName, gameType) = startSlotReq.gameId.split(":")

        val data = listOf(
                "playerHandle=999999",
                "account=FunAcct",
                "gameId=${gameId}",
                "gameName=$gameName",
                "gameType=$gameType",
                "gameSuite=Flash", // 只能是flash
                "lang=$lang"
        ).joinToString(separator = "&")

        return "http://ams-games.stg.ttms.co/casino/default/game/game.html?$data"
    }

    override fun startSlot(startSlotReq: GameValue.StartSlotReq): String {
        val tokenClient = startSlotReq.token as TTGClientToken
        val mapUtil = this.login(username = startSlotReq.username, tokenClient = tokenClient)
        val token = mapUtil.asString("token")

        /**
         * Language = Simplified Chinese (zh-cn)
         * Language = Traditional Chinese (zh-tw)
         * Language = Vietnamese (vi)
         * Language = Korean (ko)
         * Language = Japanese (ja)
         * Language = Thai (th)
         * Language = English (en)
         */
        val lang = when (startSlotReq.language) {
            Language.CN -> "zh-cn"
            Language.VI -> "vi"
            Language.TH -> "th"
            Language.EN -> "en"
            else -> "en"
        }

        val (gameId, gameName, gameType) = startSlotReq.gameId.split(":")

        val data = listOf(
                "playerHandle=$token",
                "account=MYR",
                "gameId=${gameId}",
                "gameName=$gameName",
                "gameType=$gameType",
                "gameSuite=Flash", // 只能是flash
                "lang=$lang"
        ).joinToString(separator = "&")

        return "http://ams-games.stg.ttms.co/casino/default/game/game.html?$data"
    }

    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): Any {
        val tokenClient = betOrderReq.token as TTGClientToken

        val startDate = betOrderReq.startTime.toLocalDate().toString().replace("-", "")
        val endDate = betOrderReq.endTime.toLocalDate().toString().replace("-", "")
        val startHour = betOrderReq.startTime.hour
        val endHour = betOrderReq.endTime.hour
        val data = """
            <?xml version="1.0" encoding="UTF-8"?>
            <searchdetail requestId="${UUID.randomUUID()}">
               <daterange startDate="${startDate}" startDateHour="$startHour" endDate="$endDate" endDateHour="$endHour" />
               <transaction transactionType="Game" />
            </searchdetail>
        """.trimIndent()

        val url = "${gameConstant.getOrderApiUrl(Platform.TTG)}/dataservice/datafeed/transaction/${betOrderReq.username}"

        val headers = mapOf(
                "Affiliate-Login" to tokenClient.affiliateLogin,
                "Affiliate-Id" to tokenClient.agentName,
                "Content-Type" to "text/html; charset=utf-8"
        )

        val mediaType = "text/xml".toMediaType()
        val result = okHttpUtil.doPostXml(url = url, data = data, clz = TTGValue.BetResult::class.java, headers = headers, mediaType = mediaType)
        return this.handlerBetResult(result = result).map { it.copy(originData = "") }
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {

        val tokenClient = pullBetOrderReq.token as TTGClientToken

        val startDate = pullBetOrderReq.startTime.toLocalDate().toString().replace("-", "")
        val endDate = pullBetOrderReq.endTime.toLocalDate().toString().replace("-", "")
        val startHour = pullBetOrderReq.startTime.hour
        val endHour = pullBetOrderReq.endTime.hour
        val startMinute = pullBetOrderReq.startTime.minute
        val endMinute = pullBetOrderReq.endTime.minute

        val data = """
            <?xml version="1.0" encoding="UTF-8"?>
            <searchdetail requestId="${UUID.randomUUID()}">
               <daterange startDate="$startDate" startDateHour="$startHour" startDateMinute="$startMinute" endDate="$endDate" endDateHour="$endHour" endDateMinute="$endMinute" />
               <account currency="MYR" />
               <transaction transactionType="Game" />
               <partner includeSubPartner="Y" />
            </searchdetail>
        """.trimIndent()

        val url = "${gameConstant.getOrderApiUrl(Platform.TTG)}/dataservice/datafeed/transaction/current"

        val headers = mapOf(
                "Affiliate-Login" to tokenClient.affiliateLogin,
                "Affiliate-Id" to tokenClient.agentName,
                "Content-Type" to "text/xml"
        )

        val mediaType = "text/xml".toMediaType()
        val result = okHttpUtil.doPostXml(url = url, data = data, clz = TTGValue.BetResult::class.java, headers = headers, mediaType = mediaType)
        return this.handlerBetResult(result = result)
    }

    private fun handlerBetResult(result: TTGValue.BetResult): List<BetOrderValue.BetOrderCo> {
        return result.orders.map {
            val player = it.asMap("player")
            val username = player.asString("playerId")
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.TTG, platformUsername = username)

            val detail = it.asMap("detail")
            val orderId = detail.asString("transactionId")
            val betTime = detail.asLocalDateTime("transactionDate", dateTimeFormat)
            val transactionSubType = detail.asString("transactionSubType")

            val betAmount: BigDecimal
            val winAmount: BigDecimal
            when (transactionSubType) {
                "Wager" -> {
                    betAmount = detail.asBigDecimal("amount")
                    winAmount = BigDecimal.ZERO
                }
                else -> {
                    betAmount = BigDecimal.ZERO
                    winAmount = detail.asBigDecimal("amount")
                }
            }
            val handId = detail.asString("handId")

            handId to BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, betTime = betTime, betAmount = betAmount,
                    winAmount = winAmount, originData = "", platform = Platform.TTG, settleTime = betTime)
        }.groupBy { it.first }.map {
            it.value.reduce { acc, pair ->
                val betAmount = acc.second.betAmount.plus(pair.second.betAmount)
                val winAmount = acc.second.winAmount.plus(pair.second.winAmount)
                val origin = acc.second.copy(betAmount = betAmount, winAmount = winAmount)
                val originData = objectMapper.writeValueAsString(origin)
                "" to origin.copy(originData = originData)
            }.second
        }
    }



}


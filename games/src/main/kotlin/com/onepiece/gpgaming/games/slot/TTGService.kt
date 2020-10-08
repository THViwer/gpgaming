package com.onepiece.gpgaming.games.slot

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.model.token.TTGClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.beans.value.internet.web.SlotGame
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import com.onepiece.gpgaming.games.http.U9HttpRequest
import okhttp3.MediaType.Companion.toMediaType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("UNCHECKED_CAST")
@Service
class TTGService : PlatformService() {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")
    private val log = LoggerFactory.getLogger(TTGService::class.java)

    private fun doPostXml(clientToken: TTGClientToken, method: String, data: String): OKResponse {

        val url = "${clientToken.apiPath}${method}"

        val okParam = OKParam.ofPostXml(url = url, param = data)
        return u9HttpRequest.startRequest(okParam = okParam)
    }

    private fun doGetXml(url: String, data: String): OKResponse {

        val okParam = OKParam.ofGetXml(url = url, param = data)
        return u9HttpRequest.startRequest(okParam = okParam)
    }

    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {

        val tokenClient = registerReq.token as TTGClientToken

        val newUsername = "${tokenClient.agentName}_${registerReq.username}"
        val okResponse = this.login(username = newUsername, tokenClient = tokenClient)
        return this.bindGameResponse(okResponse = okResponse) {
            newUsername
        }
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as TTGClientToken

        val url = "${clientToken.apiPath}/cip/player/${balanceReq.username}/balance"


        val okResponse = this.doGetXml(url = url, data = "")
        return this.bindGameResponse(okResponse = okResponse) {
            it.asBigDecimal("real")
        }

    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {

        val tokenClient = transferReq.token as TTGClientToken

        val data = """
            <transactiondetail uid="${transferReq.username}" amount="${transferReq.amount}" />
        """.trimIndent()
        val okResponse = this.doPostXml(clientToken = tokenClient, method = "/cip/transaction/${tokenClient.agentName}/${transferReq.orderId}", data = data)

        return this.bindGameResponse(okResponse = okResponse) {
            val flag = it.asString("retry") == "0"
            GameValue.TransferResp.of(successful = flag)
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        val tokenClient = checkTransferReq.token as TTGClientToken
        val url = "${tokenClient.apiPath}/cip/transaction/${tokenClient.agentName}/${checkTransferReq.orderId}"

        val okResponse = this.doGetXml(url = url, data = "")
        return this.bindGameResponse(okResponse = okResponse) {
            try {
                val balance = it.asBigDecimal("amount")
                GameValue.TransferResp.successful(balance = balance)
            } catch (e: Exception) {
                GameValue.TransferResp.failed()
            }
        }
    }

    private fun login(username: String, tokenClient: TTGClientToken): OKResponse {
        val data = """
            <?xml version="1.0" encoding="UTF-8"?>
            <logindetail>
               <player account="MYR" country="MY" firstName="" lastName="" userName="" nickName="" tester="0" partnerId="${tokenClient.agentName}" commonWallet="0" />
               <partners>
                  <partner partnerId="zero" partnerType="0" />
                  <partner partnerId="IG" partnerType="1" />
                  <partner partnerId="${tokenClient.agentName}" partnerType="1" />
               </partners>
            </logindetail>
        """.trimIndent()
        return this.doPostXml(clientToken = tokenClient, method = "/cip/gametoken/${username}", data = data)
    }

    override fun slotGames(token: ClientToken, launch: LaunchMethod, language: Language): List<SlotGame> {
        return when (launch) {
            LaunchMethod.Wap -> TTGGames.mobileGames
            LaunchMethod.Web -> TTGGames.pcGames
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    /**
     * Language = Simplified Chinese (zh-cn)
     * Language = Traditional Chinese (zh-tw)
     * Language = Vietnamese (vi)
     * Language = Korean (ko)
     * Language = Japanese (ja)
     * Language = Thai (th)
     * Language = English (en)
     */
    override fun startSlotDemo(startSlotReq: GameValue.StartSlotReq): GameResponse<String> {
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

        val path = "http://ams-games.stg.ttms.co/casino/default/game/game.html?$data"
        return GameResponse.of(data = path)
    }

    override fun startSlot(startSlotReq: GameValue.StartSlotReq): GameResponse<String> {
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

        val path = "${tokenClient.gamePath}?$data"
        return GameResponse.of(data = path)
    }

    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {
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

        val url = "${tokenClient.apiOrderPath}/dataservice/datafeed/transaction/${betOrderReq.username}"

        val headers = mapOf(
                "Affiliate-Login" to tokenClient.affiliateLogin,
                "Affiliate-Id" to tokenClient.agentName,
                "Content-Type" to "text/html; charset=utf-8"
        )

        val mediaType = "text/xml".toMediaType()

        val okParam = OKParam.ofPostXml(url = url, param = data, headers = headers)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)

        return this.bindGameResponse(okResponse = okResponse) {
            val content = okResponse.response
            val result = xmlMapper.readValue<TTGValue.BetResult>(content)
            this.handlerBetResult(result = result, clientToken = tokenClient).map { it.copy(originData = "") }
        }
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {

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

        val url = "${tokenClient.apiOrderPath}/dataservice/datafeed/transaction/current"

        val headers = mapOf(
                "Affiliate-Login" to tokenClient.affiliateLogin,
                "Affiliate-Id" to tokenClient.agentName,
                "Content-Type" to "text/xml"
        )

        val okParam = OKParam.ofPostXml(url = url, param = data, headers = headers).copy(mediaType = U9HttpRequest.MEDIA_TEXT_XML)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)

        return this.bindGameResponse(okResponse = okResponse) {
            val content = okResponse.response
            val result = xmlMapper.readValue<TTGValue.BetResult>(content)
            this.handlerBetResult(result = result, clientToken = tokenClient).map { it.copy(originData = "") }
        }
    }

    private fun handlerBetResult(clientToken: TTGClientToken, result: TTGValue.BetResult): List<BetOrderValue.BetOrderCo> {
        return result.orders.mapNotNull {

            val player = it.asMap("player")
            val username = player.asString("playerId")

            try {
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.TTG, platformUsername = username, prefix = clientToken.agentName)

                val detail = it.asMap("detail")
                val orderId = detail.asString("transactionId")
                val betTime = detail.asLocalDateTime("transactionDate", dateTimeFormat)
                val transactionSubType = detail.asString("transactionSubType")

                val betAmount: BigDecimal
                val winAmount: BigDecimal
                when (transactionSubType) {
                    "Wager" -> {
                        betAmount = detail.asBigDecimal("amount").abs()
                        winAmount = BigDecimal.ZERO
                    }
                    else -> {
                        betAmount = BigDecimal.ZERO
                        winAmount = detail.asBigDecimal("amount").abs()
                    }
                }

                val originData = objectMapper.writeValueAsString(it.data)
                BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, betTime = betTime, betAmount = betAmount,
                        winAmount = winAmount, originData = originData, platform = Platform.TTG, settleTime = betTime, validAmount = betAmount)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override fun queryReport(reportQueryReq: GameValue.ReportQueryReq): GameResponse<List<GameValue.PlatformReportData>> {


        val clientToken = reportQueryReq.token as TTGClientToken

        val data = """
            <searchdetail startIndexKey="" requestId="0"> 
              <daterange startDate="${reportQueryReq.startDate}" endDate="${reportQueryReq.startDate.plusDays(1)}"/>  
              <account currency="MYR"/>  
              <partner partnerId="${clientToken.agentName}" includeSubPartner="Y"/> 
            </searchdetail>
        """.trimIndent()

        val url = "${clientToken.apiOrderPath}/dataservice/datafeed/playernetwin/"

        val headers = mapOf(
                "Affiliate-Login" to clientToken.affiliateLogin,
                "Affiliate-Id" to clientToken.agentName,
                "Content-Type" to "text/xml"
        )

        val okParam = OKParam.ofPostXml(url = url, param = data, headers = headers).copy(mediaType = U9HttpRequest.MEDIA_TEXT_XML)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)


        return this.bindGameResponse(okResponse = okResponse) {
            val content = okResponse.response
            val result = xmlMapper.readValue<TTGValue.BetResult>(content)

            result.orders.map {
                val username = it.asMap("player").asString("playerId")
                val totalBet = it.asMap("summary").asBigDecimal("totalWager")
                val totalWin = it.asMap("summary").asBigDecimal("totalWin")
//              it.asMap("summary").asBigDecimal("winPercent") // 勝率
//              it.asMap("summary").asBigDecimal("avgWagerHand") // 平臺下注金額

                val originData = objectMapper.writeValueAsString(it.data)
                GameValue.PlatformReportData(username = username, platform = Platform.TTG, bet = totalBet, win = totalWin, originData = originData)
            }
        }
    }

}


//fun main() {
//    val xml = """
//        <searchdetail requestId="100000" startIndexKey="" isMore="N" pageSize="10000" totalRecords="1">
//          <details>
//            <netwin>
//              <player country="US" partnerId="zero" playerId="9000"/>
//              <summary avgWagerHand="0.51" winPercent="79.00" totalWin="10.39" totalWager="13.20" numHands="26" numWagers="26" currency="USD"/>
//            </netwin>
//          </details>
//        </searchdetail>
//
//    """.trimIndent()
//
//    val xmlMapper = XmlMapper()
//            .registerKotlinModule()
//    val result = xmlMapper.readValue<TTGValue.BetResult>(xml)
//
//    val reports = result.orders.map {
//        val username = it.asMap("player").asString("playerId")
//        val totalBet = it.asMap("summary").asBigDecimal("totalWager")
//        val totalWin = it.asMap("summary").asBigDecimal("totalWin")
////        it.asMap("summary").asBigDecimal("winPercent") // 勝率
////        it.asMap("summary").asBigDecimal("avgWagerHand") // 平臺下注金額
//
//        GameValue.PlatformReportData(username = username, platform = Platform.TTG, bet = totalBet, win = totalWin, originData = "")
//    }
//
//    println(reports)
//    println("--------")
//    println("--------")
//    println("--------")
//    println("--------")
//}

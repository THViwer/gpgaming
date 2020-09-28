package com.onepiece.gpgaming.games.slot

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
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
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
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
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.TTG, platformUsername = username, prefix = "${clientToken.agentName}")

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
                        winAmount = detail.asBigDecimal("amount").abs()
                    }
                }
//            val handId = detail.asString("handId")

                val originData = objectMapper.writeValueAsString(it.data)
                BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, betTime = betTime, betAmount = betAmount,
                        winAmount = winAmount, originData = originData, platform = Platform.TTG, settleTime = betTime, validAmount = betAmount)
            } catch (e: Exception) {

//                if (e is java.lang.NumberFormatException) {
//                    null
//                } else {
                    throw e
//                }
            }
        }
//                .groupBy { it.first }.map {
//            it.value.reduce { acc, pair ->
//                val betAmount = acc.second.betAmount.plus(pair.second.betAmount)
//                val winAmount = acc.second.winAmount.plus(pair.second.winAmount)
//                val origin = acc.second.copy(betAmount = betAmount, winAmount = winAmount)
//                val originData = objectMapper.writeValueAsString(origin)
//                "" to origin.copy(originData = originData)
//            }.second
//        }
    }


}
//
//fun main() {
//
//
//    val content = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><searchdetail totalRecords="66" pageSize="66" requestId="ef6f71bd-e9f7-4753-8c56-bfa6670d3af3"><details><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957254" transactionDate="20200928 15:29:03" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791599" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957256" transactionDate="20200928 15:29:03" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791599" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957323" transactionDate="20200928 15:29:06" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791603" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957325" transactionDate="20200928 15:29:06" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791603" amount="1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957352" transactionDate="20200928 15:29:07" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791604" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957354" transactionDate="20200928 15:29:07" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791604" amount="0.60"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957384" transactionDate="20200928 15:29:08" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791606" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957386" transactionDate="20200928 15:29:08" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791606" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957422" transactionDate="20200928 15:29:09" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791608" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957424" transactionDate="20200928 15:29:09" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791608" amount="5.70"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957460" transactionDate="20200928 15:29:10" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791611" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957462" transactionDate="20200928 15:29:10" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791611" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957492" transactionDate="20200928 15:29:11" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791612" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957494" transactionDate="20200928 15:29:11" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791612" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957531" transactionDate="20200928 15:29:12" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791614" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957533" transactionDate="20200928 15:29:12" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791614" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957584" transactionDate="20200928 15:29:14" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791616" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957586" transactionDate="20200928 15:29:14" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791616" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957614" transactionDate="20200928 15:29:15" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791617" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957616" transactionDate="20200928 15:29:15" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791617" amount="0.84"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957653" transactionDate="20200928 15:29:16" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791620" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957655" transactionDate="20200928 15:29:16" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791620" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957683" transactionDate="20200928 15:29:17" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791621" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957685" transactionDate="20200928 15:29:17" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791621" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957728" transactionDate="20200928 15:29:18" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791623" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957730" transactionDate="20200928 15:29:18" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791623" amount="1.08"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957765" transactionDate="20200928 15:29:19" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791625" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957767" transactionDate="20200928 15:29:19" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791625" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957783" transactionDate="20200928 15:29:20" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791626" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957785" transactionDate="20200928 15:29:20" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791626" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957835" transactionDate="20200928 15:29:21" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791628" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957837" transactionDate="20200928 15:29:21" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791628" amount="2.64"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957883" transactionDate="20200928 15:29:23" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791631" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957885" transactionDate="20200928 15:29:23" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791631" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957917" transactionDate="20200928 15:29:24" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791632" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957919" transactionDate="20200928 15:29:24" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791632" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957945" transactionDate="20200928 15:29:25" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791633" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957947" transactionDate="20200928 15:29:25" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791633" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957982" transactionDate="20200928 15:29:26" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791637" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290957985" transactionDate="20200928 15:29:26" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791637" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958004" transactionDate="20200928 15:29:27" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791638" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958006" transactionDate="20200928 15:29:27" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791638" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958056" transactionDate="20200928 15:29:28" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791640" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958059" transactionDate="20200928 15:29:28" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791640" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958093" transactionDate="20200928 15:29:29" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791642" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958095" transactionDate="20200928 15:29:29" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791642" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958125" transactionDate="20200928 15:29:30" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791646" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958127" transactionDate="20200928 15:29:30" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791646" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958163" transactionDate="20200928 15:29:32" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791647" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958165" transactionDate="20200928 15:29:32" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791647" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958194" transactionDate="20200928 15:29:33" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791648" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958196" transactionDate="20200928 15:29:33" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791648" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958223" transactionDate="20200928 15:29:34" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791651" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958225" transactionDate="20200928 15:29:34" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791651" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958268" transactionDate="20200928 15:29:35" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945791652" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290958270" transactionDate="20200928 15:29:35" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945791652" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290960507" transactionDate="20200928 15:30:44" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945795198" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290960509" transactionDate="20200928 15:30:44" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945795198" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290960569" transactionDate="20200928 15:30:45" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945795203" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290960571" transactionDate="20200928 15:30:45" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945795203" amount="0.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290960617" transactionDate="20200928 15:30:46" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945795205" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290960619" transactionDate="20200928 15:30:46" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945795205" amount="1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290960660" transactionDate="20200928 15:30:48" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945795206" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290960663" transactionDate="20200928 15:30:48" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945795206" amount="3.00"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290960699" transactionDate="20200928 15:30:49" currency="MYR" game="MadMonkeyH5" transactionSubType="Wager" handId="5945795210" amount="-1.50"/></transaction><transaction><player playerId="U996_01008067pg" partnerId="U996"/><detail transactionId="18290960701" transactionDate="20200928 15:30:49" currency="MYR" game="MadMonkeyH5" transactionSubType="Resolve" handId="5945795210" amount="3.00"/></transaction></details></searchdetail>"""
//
//    val result = xmlMapper.readValue<TTGValue.BetResult>(content)
//    val x = handlerBetResult2(result = result).map { it.copy(originData = "") }
//    println(x)
//
//}
//
//private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")
//fun objectMapper(): ObjectMapper {
//    val objectMapper = jacksonObjectMapper()
//            .registerModule(ParameterNamesModule())
//            .registerModule(Jdk8Module())
//            .registerModule(JavaTimeModule())
//
//    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
//    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
////        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//
//    return objectMapper
//
//}
//
//
//fun xmlMapper(): XmlMapper {
//    val xmlMapper = XmlMapper()
//    xmlMapper.registerModule(KotlinModule())
//    return xmlMapper
//}
//val objectMapper = objectMapper()
//val xmlMapper = xmlMapper()
//
//private fun handlerBetResult2(result: TTGValue.BetResult): List<BetOrderValue.BetOrderCo> {
//    return result.orders.mapNotNull {
//
//        val player = it.asMap("player")
//        val username = player.asString("playerId")
//
//        try {
//            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.TTG, platformUsername = username, prefix = "U996")
//
//            val detail = it.asMap("detail")
//            val orderId = detail.asString("transactionId")
//            val betTime = detail.asLocalDateTime("transactionDate", dateTimeFormat)
//            val transactionSubType = detail.asString("transactionSubType")
//
//            val betAmount: BigDecimal
//            val winAmount: BigDecimal
//            when (transactionSubType) {
//                "Wager" -> {
//                    betAmount = detail.asBigDecimal("amount")
//                    winAmount = BigDecimal.ZERO
//                }
//                else -> {
//                    betAmount = BigDecimal.ZERO
//                    winAmount = detail.asBigDecimal("amount").abs()
//                }
//            }
////            val handId = detail.asString("handId")
//
//            val originData = objectMapper.writeValueAsString(it.data)
//            BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, betTime = betTime, betAmount = betAmount,
//                    winAmount = winAmount, originData = originData, platform = Platform.TTG, settleTime = betTime, validAmount = betAmount)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            if (e is java.lang.NumberFormatException) {
//                null
//            } else {
//                throw e
//            }
//        }
//    }
//
//}
//


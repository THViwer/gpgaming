package com.onepiece.gpgaming.player.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.SpadeGamingClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.PlatformUsernameUtil
import com.onepiece.gpgaming.core.service.BetOrderService
import com.onepiece.gpgaming.games.bet.JacksonMapUtil
import com.onepiece.gpgaming.games.bet.MapUtil
import com.onepiece.gpgaming.player.controller.basic.BasicController
import com.onepiece.gpgaming.player.controller.value.PlatformAuthValue
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.*


@RestController
@RequestMapping
class PlatformAuthApiController(
        private val objectMapper: ObjectMapper,
        private val betOrderService: BetOrderService
): BasicController(), PlatformAuthApi {

    private val log = LoggerFactory.getLogger(PlatformAuthApiController::class.java)

    @PostMapping("/mega", produces = ["application/json;charset=utf-8"])
    override fun login(
            @RequestParam("d") d: Int): String {

        val json = String(getRequest().inputStream.readBytes())
        log.info("厅主：$d, 请求参数:$json")

        val mapUtil = objectMapper.readValue<JacksonMapUtil>(json.replace("json=", "")).mapUtil

        val username = mapUtil.asMap("params").asString("loginId")
        val password = mapUtil.asMap("params").asString("password")


        val successful = platformMemberService.login(platform = Platform.Mega, username = username, password = password)
        log.info("登陆mega,用户名：${username}是否成功:$successful")

        val successCode = if (successful) 1 else 0
        val msg = if (successful) "登陆成功" else "用户名或密码错误"

        val data = """
            {
                "id": "${UUID.randomUUID()}",
                "result": {
                    "success": "$successCode",
                    "sessionId": "${UUID.randomUUID()}",
                    "msg": "$msg" 
                    },
                "error": null,
                "jsonrpc": "2.0"
            }
        """.trimIndent()
        return data

//        return LoginResult(success = "1", sessionId = UUID.randomUUID().toString(), msg = "login success")
    }


    @PostMapping("/ebet", produces = ["application/json;charset=utf-8"])
    override fun login(@RequestBody data: Map<String, Any>): PlatformAuthValue.EBetResponse {
        log.info("ebet 获得登陆数据：$data")

        val mapUtil = MapUtil.instance(data)
        val username = mapUtil.asString("username")
        val accessToken = mapUtil.asString("accessToken")

        val sign = DigestUtils.md5Hex("$username:ebet:1")

        log.info("签名校验：accessToken=$accessToken, sign=$sign, 是否通过：${accessToken == sign}")

        return PlatformAuthValue.EBetResponse(accessToken = accessToken, username = username, status = "200", nickname = username)
    }



    @GetMapping("/mega")
    override fun download(@RequestHeader("clientId", defaultValue = "1") clientId: Int): String {
        return gameApi.getAppDownload(clientId = clientId, platform = Platform.Mega)
    }


    data class LoginReq(
            val random: String,

            val digest: String,

            val sn: String,

            val loginId: String,

            val password: String

    )

    data class LoginResult(
            val success: String,

            val sessionId: String,

            val msg: String
    )

    @GetMapping("/cmd", produces = ["application/xml;charset=utf-8"])
    override fun cmdLogin(
            @RequestParam("token") token: String?,
            @RequestParam("secret_key") secret_key: String?
    ): String {

        log.info("cmd 请求：token=$token, secret_key = $secret_key")

        val request = getRequest()
        request.parameterMap.map {
            log.info(it.key, "${it.value}")
        }

        log.info(request.requestURI)
        log.info(request.contextPath)
        log.info(request.requestURL.toString())


        return """
            <?xml version="1.0" encoding="UTF-8"?> 
                <authenticate> 
                <member_id>TestUser001</member_id> 
                <status_code>0</status_code> 
                <message>Success</message> 
            </authenticate>
        """.trimIndent()

    }

    @PostMapping("/spadeGaming")
    override fun spadeGamingLogin(@RequestBody request: PlatformAuthValue.SpadeGamingRequest): PlatformAuthValue.SpadeGamingResponse {
        log.info("请求参数：${request}")
        val binds = platformBindService.find(Platform.SpadeGaming)
        val bind = binds.first { (it.clientToken as SpadeGamingClientToken).memberCode == request.merchantCode }
        log.info("绑定平台信息:$bind")

        val clientToken = bind.clientToken as SpadeGamingClientToken
        val acctInfo = PlatformAuthValue.SpadeGamingResponse.AcctInfo(acctId = request.acctId, balance = BigDecimal.ZERO, userName = "hello", currency = "MYR", siteId = clientToken.siteId)
        val response = PlatformAuthValue.SpadeGamingResponse(merchantCode = request.merchantCode, msg = "", acctInfo = acctInfo)
        log.info("返回信息:$response")
        return response
    }

    @PostMapping("/png/order")
    override fun pngCallData(@RequestBody jacksonMapUtil: JacksonMapUtil) {
        log.info("png 开始调用方法")

        log.info("png post 数据： ${objectMapper.writeValueAsString(jacksonMapUtil)}")

        val mapUtil = jacksonMapUtil.mapUtil
        val orders = mapUtil.asList("Messages").filter {
            val messageType = it.asString("MessageType")
            messageType == "3" || messageType == "4"
        }.map { bet ->

            val orderId = bet.asString("TransactionId")
            val messageType = bet.asString("MessageType")

            val betAmount: BigDecimal
            val winAmount: BigDecimal
            when (messageType) {
                "3" -> {
                    betAmount = bet.asBigDecimal("Amount")
                    winAmount = BigDecimal.ZERO
                }
                "4" -> {
                    betAmount = BigDecimal.ZERO
                    winAmount = bet.asBigDecimal("Amount")
                }
                else -> error(OnePieceExceptionCode.DATA_FAIL)
            }
            val username = bet.asString("ExternalUserId")
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.PNG, platformUsername = username)
            val betTime = bet.asLocalDateTime("Time")

            val originData = objectMapper.writeValueAsString(bet)
            BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, platform = Platform.PNG, betAmount = betAmount, winAmount = winAmount,
                    originData = originData, betTime = betTime, settleTime = betTime)
        }

        betOrderService.batch(orders)
    }

//    fun main() {
//        val json = "{\"Messages\":[{\"TransactionId\":190663,\"Status\":1,\"Amount\":6.40,\"Time\":\"2019-12-12T05:32:01.7388318\",\"ProductGroup\":8835,\"ExternalUserId\":\"01000016pg\",\"GamesessionId\":24233,\"GameId\":100333,\"RoundId\":1004255,\"Currency\":\"MYR\",\"ExternalTransactionId\":\"190663\",\"Balance\":93.6000,\"Channel\":2,\"MessageId\":\"4961#43\",\"MessageType\":3,\"MessageTimestamp\":\"2019-12-12T05:32:01\"},{\"TransactionId\":190664,\"Status\":1,\"Amount\":4.48,\"Time\":\"2019-12-12T05:32:06.0220528\",\"ProductGroup\":8835,\"ExternalUserId\":\"01000016pg\",\"GamesessionId\":24233,\"GamesessionState\":0,\"GameId\":100333,\"RoundId\":1004255,\"RoundData\":null,\"RoundLoss\":6.40,\"JackpotLoss\":0.0,\"JackpotGain\":0.0,\"Currency\":\"MYR\",\"ExternalTransactionId\":\"190664\",\"Balance\":98.0800,\"NumRounds\":1,\"TotalLoss\":6.40,\"TotalGain\":4.48,\"ExternalFreegameId\":null,\"Channel\":2,\"MessageId\":\"4961#44\",\"MessageType\":4,\"MessageTimestamp\":\"2019-12-12T05:32:06\"},{\"TransactionId\":190665,\"Status\":1,\"Amount\":6.40,\"Time\":\"2019-12-12T05:32:06.7542985\",\"ProductGroup\":8835,\"ExternalUserId\":\"01000016pg\",\"GamesessionId\":24233,\"GameId\":100333,\"RoundId\":1004256,\"Currency\":\"MYR\",\"ExternalTransactionId\":\"190665\",\"Balance\":91.6800,\"Channel\":2,\"MessageId\":\"4961#45\",\"MessageType\":3,\"MessageTimestamp\":\"2019-12-12T05:32:06\"},{\"TransactionId\":190666,\"Status\":1,\"Amount\":1.28,\"Time\":\"2019-12-12T05:32:11.2263195\",\"ProductGroup\":8835,\"ExternalUserId\":\"01000016pg\",\"GamesessionId\":24233,\"GamesessionState\":0,\"GameId\":100333,\"RoundId\":1004256,\"RoundData\":null,\"RoundLoss\":6.40,\"JackpotLoss\":0.0,\"JackpotGain\":0.0,\"Currency\":\"MYR\",\"ExternalTransactionId\":\"190666\",\"Balance\":92.9600,\"NumRounds\":2,\"TotalLoss\":12.80,\"TotalGain\":5.76,\"ExternalFreegameId\":null,\"Channel\":2,\"MessageId\":\"4961#46\",\"MessageType\":4,\"MessageTimestamp\":\"2019-12-12T05:32:11\"},{\"TransactionId\":190667,\"Status\":1,\"Amount\":6.40,\"Time\":\"2019-12-12T05:32:12.0822553\",\"ProductGroup\":8835,\"ExternalUserId\":\"01000016pg\",\"GamesessionId\":24233,\"GameId\":100333,\"RoundId\":1004257,\"Currency\":\"MYR\",\"ExternalTransactionId\":\"190667\",\"Balance\":86.5600,\"Channel\":2,\"MessageId\":\"4961#47\",\"MessageType\":3,\"MessageTimestamp\":\"2019-12-12T05:32:12\"},{\"TransactionId\":190668,\"Status\":1,\"Amount\":0.0,\"Time\":\"2019-12-12T05:32:12.0978511\",\"ProductGroup\":8835,\"ExternalUserId\":\"01000016pg\",\"GamesessionId\":24233,\"GamesessionState\":0,\"GameId\":100333,\"RoundId\":1004257,\"RoundData\":null,\"RoundLoss\":6.40,\"JackpotLoss\":0.0,\"JackpotGain\":0.0,\"Currency\":\"MYR\",\"ExternalTransactionId\":\"190668\",\"Balance\":86.5600,\"NumRounds\":3,\"TotalLoss\":19.20,\"TotalGain\":5.76,\"ExternalFreegameId\":null,\"Channel\":2,\"MessageId\":\"4961#48\",\"MessageType\":4,\"MessageTimestamp\":\"2019-12-12T05:32:12\"}]}"
//        val objectMapper = jacksonObjectMapper()
//
//        val mapUtil = objectMapper.readValue<JacksonMapUtil>(json).mapUtil
//
//        val orders = mapUtil.asList("Messages").filter {
//            val messageType = it.asString("MessageType")
//            messageType == "3" || messageType == "4"
//        }.map { bet ->
//
//            val orderId = bet.asString("TransactionId")
//            val messageType = bet.asString("MessageType")
//
//            val betAmount: BigDecimal
//            val winAmount: BigDecimal
//            when (messageType) {
//                "3" -> {
//                    betAmount = bet.asBigDecimal("Amount")
//                    winAmount = BigDecimal.ZERO
//                }
//                "4" -> {
//                    betAmount = BigDecimal.ZERO
//                    winAmount = bet.asBigDecimal("Amount")
//                }
//                else -> error(OnePieceExceptionCode.DATA_FAIL)
//            }
//            val username = bet.asString("ExternalUserId")
//            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.PNG, platformUsername = username)
//            val betTime = bet.asLocalDateTime("Time")
//
//            val originData = objectMapper.writeValueAsString(bet)
//            BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, platform = Platform.PNG, betAmount = betAmount, winAmount = winAmount,
//                    originData = originData, betTime = betTime, settleTime = betTime)
//        }
//
//        println(orders)
//
//    }
}
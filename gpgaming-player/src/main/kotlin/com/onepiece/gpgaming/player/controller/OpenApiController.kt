package com.onepiece.gpgaming.player.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.GamePlayClientToken
import com.onepiece.gpgaming.beans.model.token.SpadeGamingClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.core.service.BetOrderService
import com.onepiece.gpgaming.games.bet.JacksonMapUtil
import com.onepiece.gpgaming.player.controller.basic.BasicController
import com.onepiece.gpgaming.player.controller.value.PlatformAuthValue
import com.onepiece.gpgaming.utils.StringUtil
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("/open")
class OpenApiController(
        private val objectMapper: ObjectMapper,
        private val betOrderService: BetOrderService
) : BasicController(), OpenApi {

    private val log = LoggerFactory.getLogger(OpenApiController::class.java)

    @RequestMapping(path = ["/gameplay"], produces = ["application/xml;charset=utf-8"])
    override fun gamePlayLogin(): String {

        val request = getRequest()

        val ticket = request.getParameter("ticket")
        log.info("ticket = $ticket")

        val originData = String(Base64.decodeBase64(ticket.toByteArray()))
        log.info("origin data = $originData")

        val platformUsername = originData.split(":").first()
        val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.GamePlay, platformUsername = platformUsername)

        val platformBind = platformBindService.find(clientId, Platform.GamePlay)


        val clientToken = platformBind.clientToken as GamePlayClientToken
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <resp>
             <error_code>0</error_code>
             <error_msg></error_msg>
             <cust_id>${platformUsername}</cust_id>
             <cust_name>${platformUsername}</cust_name>
             <currency_code>${clientToken.currency}</currency_code>
             <language>en-us</language>
             <test_cust>true</test_cust>
             <country>${clientToken.currency}</country>
             <date_of_birth>29-09-1989</date_of_birth>
             <ip>${StringUtil.generateNumNonce(2)}.${StringUtil.generateNumNonce(2)}.${StringUtil.generateNumNonce(2)}.${StringUtil.generateNumNonce(2)}</ip>
            </resp>
        """.trimIndent()
    }


    @PostMapping("/mega/{clientId}", produces = ["application/json;charset=utf-8"])
    override fun login(
            @PathVariable("clientId") clientId: Int): String {

        val json = String(getRequest().inputStream.readBytes())
        log.info("?????????${getClientId()}, ????????????:$json")

        val mapUtil = objectMapper.readValue<JacksonMapUtil>(json.replace("json=", "")).mapUtil

        val username = mapUtil.asMap("params").asString("loginId")
        val password = mapUtil.asMap("params").asString("password")


        val successful = platformMemberService.login(platform = Platform.Mega, username = username, password = password)
        log.info("??????mega,????????????${username}????????????:$successful")

        val successCode = if (successful) 1 else 0
        val msg = if (successful) "????????????" else "????????????????????????"

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
    }

    @GetMapping("/mega")
    override fun download(@RequestHeader("clientId", defaultValue = "1") clientId: Int): String {
        return gameApi.getAppDownload(clientId = clientId, platform = Platform.Mega)
    }


    @GetMapping("/cmd", produces = ["application/xml;charset=utf-8"])
    override fun cmdLogin(
            @RequestParam("token") token: String?,
            @RequestParam("secret_key") secret_key: String?
    ): String {

        log.info("cmd ?????????token=$token, secret_key = $secret_key")

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
        log.info("???????????????${request}")
        val binds = platformBindService.find(Platform.SpadeGaming)
        val bind = binds.first { (it.clientToken as SpadeGamingClientToken).memberCode == request.merchantCode }
        log.info("??????????????????:$bind")

        val clientToken = bind.clientToken as SpadeGamingClientToken
        val acctInfo = PlatformAuthValue.SpadeGamingResponse.AcctInfo(acctId = request.acctId, balance = BigDecimal.ZERO, userName = "hello", currency = "MYR", siteId = clientToken.siteId)
        val response = PlatformAuthValue.SpadeGamingResponse(merchantCode = request.merchantCode, msg = "", acctInfo = acctInfo)
        log.info("????????????:$response")
        return response
    }

    @PostMapping("/png/order")
    override fun pngCallData(@RequestBody jacksonMapUtil: JacksonMapUtil) {
        log.info("png ??????????????????")

        log.info("png post ????????? ${objectMapper.writeValueAsString(jacksonMapUtil)}")

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
                    originData = originData, betTime = betTime, settleTime = betTime, validAmount = betAmount)
        }

        betOrderService.batch(orders)
    }
}
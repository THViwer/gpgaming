package com.onepiece.treasure.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.SpadeGamingClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.PlatformAuthValue
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.core.service.BetOrderService
import com.onepiece.treasure.games.bet.JacksonMapUtil
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("/open")
class OpenApiController(
        private val objectMapper: ObjectMapper,
        private val betOrderService: BetOrderService
) : BasicController(), OpenApi {

    private val log = LoggerFactory.getLogger(OpenApiController::class.java)

    @RequestMapping("/gameplay", produces = ["application/json;charset=utf-8"])
    override fun gamePlayLogin(): String {

        val request = getRequest()

        request.parameterMap.forEach {

            log.info("url key : ${it.key}, value: ${it.value}")
        }

        val data = request.inputStream.readBytes().let { String(it) }
        log.info("game play 收到消息:$data")


        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <resp>
             <error_code>0</error_code>
             <cust_id>GPGAMING88</cust_id>
             <cust_name>Dummy</cust_name>
             <currency_code>IDR</currency_code>
             <language>en-us</language>
             <test_cust>false</test_cust>
             <country>US</country>
             <date_of_birth>29-09-1989</date_of_birth>
             <ip>1.1.1.1</ip>
            </resp>
        """.trimIndent()
    }


    @PostMapping("/mega/{clientId}", produces = ["application/json;charset=utf-8"])
    override fun login(
            @PathVariable("clientId") clientId: Int): String {

        val json = String(getRequest().inputStream.readBytes())
        log.info("厅主：$clientId, 请求参数:$json")

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
}
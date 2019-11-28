package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.token.SpadeGamingClientToken
import com.onepiece.treasure.controller.value.PlatformAuthValue
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.games.GameApi
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping
class PlatformAuthApiController(
        private val gameApi: GameApi,
        private val platformBindService: PlatformBindService
): PlatformAuthApi {

    private val log = LoggerFactory.getLogger(PlatformAuthApiController::class.java)

    @PostMapping("/mega")
    override fun login(
            @RequestParam("d") d: Int,
            @RequestBody loginReq: LoginReq): LoginResult {

        log.info("厅主：$d, 请求参数:$loginReq")

        return LoginResult(success = "1", sessionId = UUID.randomUUID().toString(), msg = "login success")
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

    @GetMapping("/cmd")
    override fun cmdLogin(
            @RequestParam("token") token: String,
            @RequestParam("secret_key") secret_key: String
    ): String {

        log.info("cmd 请求：token=$token, secret_key = $secret_key")

        return """
            <?xml version="1.0" encoding="UTF-8"?> 
                <authenticate> 
                <member_id>TestUser001</member_id> 
                <status_code>0</status_code> 
                <message>Success</ message > 
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
        val response = PlatformAuthValue.SpadeGamingResponse(merchantCode = request.merchantCode, msg = "", acctInfo =  acctInfo)
        log.info("返回信息:$response")
        return response
    }
}
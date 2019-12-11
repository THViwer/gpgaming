package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.token.SpadeGamingClientToken
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.PlatformAuthValue
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping
class PlatformAuthApiController: BasicController(), PlatformAuthApi {

    private val log = LoggerFactory.getLogger(PlatformAuthApiController::class.java)

    @PostMapping("/mega")
    override fun login(
            @RequestParam("d") d: Int,
            @RequestBody loginReq: LoginReq): LoginResult {

        log.info("厅主：$d, 请求参数:$loginReq")

        val successful = platformMemberService.login(platform = Platform.Mega, username = loginReq.loginId, password = loginReq.password)
        log.info("登陆mega,用户名：${loginReq.loginId}是否成功:$successful")

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
        val response = PlatformAuthValue.SpadeGamingResponse(merchantCode = request.merchantCode, msg = "", acctInfo =  acctInfo)
        log.info("返回信息:$response")
        return response
    }

    @PostMapping("/png/order")
    override fun pngCallData() {
        log.info("png 开始调用方法")

        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val json = String(request.inputStream.readBytes())
        log.info("png post 数据： $json")


    }
}
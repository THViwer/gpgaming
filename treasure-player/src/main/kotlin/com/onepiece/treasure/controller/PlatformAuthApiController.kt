package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.games.GameApi
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping
class PlatformAuthApiController(
        private val gameApi: GameApi
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

        //TODO 检查token 和 secret_key

        return """
            <?xml version="1.0" encoding="UTF-8"?> 
                <authenticate> 
                <member_id>TestUser001</member_id> 
                <status_code>0</status_code> 
                <message>Success</ message > 
            </authenticate>
        """.trimIndent()

    }
}
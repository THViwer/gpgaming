package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.games.GameApi
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/mega")
class MegaApiController(
        private val gameApi: GameApi
): MegaApi {

    private val log = LoggerFactory.getLogger(MegaApiController::class.java)

    @PostMapping
    override fun login(
            @RequestParam("d") d: Int,
            @RequestBody loginReq: LoginReq): LoginResult {

        log.info("厅主：$d, 请求参数:$loginReq")

        return LoginResult(success = "1", sessionId = UUID.randomUUID().toString(), msg = "login success")
    }

    @GetMapping
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

}
package com.onepiece.treasure.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/mega")
class MegaController {

    private val log = LoggerFactory.getLogger(MegaController::class.java)

    @PostMapping
    fun login(
            @RequestParam("d") d: Int,
            @RequestBody loginReq: LoginReq): LoginResult {

        log.info("厅主：$d, 请求参数:$loginReq")

        return LoginResult(success = "1", sessionId = UUID.randomUUID().toString(), msg = "login success")

    }

    data class LoginReq(
            val randon: String,

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
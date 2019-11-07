package com.onepiece.treasure.task.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.database.ClientCo
import com.onepiece.treasure.beans.value.database.PlatformBindCo
import com.onepiece.treasure.core.service.ClientService
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.games.GamePlatformUtil
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/client")
class ClientController(
        private val clientService: ClientService,
        private val platformBindService: PlatformBindService,

        private val gamePlatformUtil: GamePlatformUtil
) {

    @GetMapping("/init")
    fun initClient(
            @RequestParam("username") username: String,
            @RequestParam("password") password: String
    ) {
        val clientCo = ClientCo(brand = username, username = username, password = password, loginTime = LocalDateTime.now())
        clientService.create(clientCo)
    }

    @GetMapping("/open")
    fun openPlatform(
            @RequestParam("clientId") clientId: Int,
            @RequestParam("platform") platform: Platform
    ) {

        val (username, password) = when (platform) {
            Platform.Kiss918 -> {
                val password = "fsf"
                gamePlatformUtil.getPlatformBuild(platform).gameApi.register("username", password) to password
            }
            else -> null to null
        }

        val platformBindCo = PlatformBindCo(clientId = clientId, platform = platform, username = username, password = password)
        platformBindService.create(platformBindCo)
    }


}
package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.beans.model.GamePlatform
import com.onepiece.gpgaming.beans.value.database.GamePlatformValue
import com.onepiece.gpgaming.core.service.GamePlatformService
import com.onepiece.gpgaming.core.utils.PolUtil
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/gamePlatform")
class GamePlatformApiController(
        private val gamePlatformService: GamePlatformService,
        private val polUtil: PolUtil
) : GamePlatformApi {

    @PostMapping
    override fun create(@RequestBody gamePlatformCo: GamePlatformValue.GamePlatformCo) {
        gamePlatformService.create(gamePlatformCo)
    }

    @PutMapping
    override fun update(@RequestBody gamePlatformUo: GamePlatformValue.GamePlatformUo) {
        gamePlatformService.update(gamePlatformUo)
    }

    @GetMapping
    override fun list(): List<GamePlatform> {
        return gamePlatformService.all()
    }

    @GetMapping("/pull/job/detail")
    override fun pullJobDetail(@RequestParam("clientId") clientId: Int): List<PolUtil.PullOrderLog> {
        return polUtil.getPol(clientId = clientId)
    }
}
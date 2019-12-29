package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.value.internet.web.PlatformVo
import com.onepiece.gpgaming.core.service.GamePlatformService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/platform")
class ClientPlatformApiController(
        private val gamePlatformService: GamePlatformService
): BasicController(), ClientPlatformApi {

    @GetMapping
    override fun all(): List<PlatformVo> {
        val clientId = getClientId()
        val clientBinds = platformBindService.findClientPlatforms(clientId)
                .map { it.platform to it }
                .toMap()
        val gamePlatforms = gamePlatformService.all()

        return Platform.all().map {
            val clientBind = clientBinds[it]

            val gamePlatform = it.getGamePlatform(gamePlatforms)
            if (clientBind != null) {
                PlatformVo(id = clientBind.id, platform = clientBind.platform, status = clientBind.status, open = true,
                        gamePlatform = gamePlatform)
            } else {
                PlatformVo(id = -1, platform = it, status = Status.Stop, open = false,
                        gamePlatform = gamePlatform)
            }
        }
    }

    @GetMapping("/open")
    override fun openList(): List<PlatformVo> {
        val clientId = getClientId()
        val gamePlatforms = gamePlatformService.all()

        val clientBinds = platformBindService.findClientPlatforms(clientId)

        return clientBinds.map {
            PlatformVo(id = it.id, platform = it.platform, status = it.status, open = true, gamePlatform = it.platform.getGamePlatform(gamePlatforms))
        }
    }

    //    @PutMapping
//    override fun update(@RequestBody platformUoReq: PlatformUoReq) {
//
//        val platform = platformBindService.findClientPlatforms(clientId).find { it.id == platformUoReq.id }
//        checkNotNull(platform) { OnePieceExceptionCode.AUTHORITY_FAIL }
//
//        val platformBindUo = PlatformBindUo(id = platformUoReq.id, status = platformUoReq.status, earnestBalance = null, username = null, password = null, tokenJson = null)
//        platformBindService.update(platformBindUo)
//    }
}
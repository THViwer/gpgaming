package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.value.database.PlatformBindUo
import com.onepiece.gpgaming.beans.value.internet.web.PlatformValue
import com.onepiece.gpgaming.beans.value.internet.web.PlatformVo
import com.onepiece.gpgaming.core.service.GamePlatformService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientApiController(
        private val gamePlatformService: GamePlatformService
): BasicController(), ClientApi {

    @GetMapping("/platform")
    override fun all(): List<PlatformVo> {
        val clientId = getClientId()
        val clientBinds = platformBindService.findClientPlatforms(clientId)
                .map { it.platform to it }
                .toMap()


        return Platform.all().map {
            val clientBind = clientBinds[it]

            val platformBind = clientBinds[it] ?: error("")

            if (clientBind != null) {
                PlatformVo(id = clientBind.id, platform = clientBind.platform, status = clientBind.status, open = true,
                        hot = clientBind.hot, new = clientBind.new, platformBind = platformBind)
            } else {
                PlatformVo(id = -1, platform = it, status = Status.Stop, open = false,
                        hot = false, new = false, platformBind = platformBind)
            }
        }
    }

    @GetMapping("/platform/open")
    override fun openList(): List<PlatformVo> {
        val clientId = getClientId()

        val clientBinds = platformBindService.findClientPlatforms(clientId)

        return clientBinds.map {
            PlatformVo(id = it.id, platform = it.platform, status = it.status, open = true, hot = it.hot, new = it.new, platformBind = it)
        }
    }

    @PutMapping("/platform")
    override fun update(@RequestBody uo: PlatformValue.PlatformBindUo) {
        val bindUo = PlatformBindUo(id = uo.id, username = null, hot = uo.hot, new = uo.new, password = null, tokenJson = null,
                earnestBalance = null, status = null, name = uo.name, icon = uo.icon, disableIcon = uo.disableIcon, originIcon = uo.originIcon,
                originIconOver = uo.originIconOver, mobileIcon = uo.mobileIcon, mobileDisableIcon = uo.mobileDisableIcon, platformDetailIcon = uo.platformDetailIcon,
                platformDetailIconOver = uo.platformDetailIconOver)
        platformBindService.update(bindUo)
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
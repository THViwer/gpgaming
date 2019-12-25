package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.value.internet.web.PlatformVo
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/platform")
class ClientPlatformApiController: BasicController(), ClientPlatformApi {

    @GetMapping
    override fun all(): List<PlatformVo> {
        val clientId = getClientId()
        val clientBinds = platformBindService.findClientPlatforms(clientId)
                .map { it.platform to it }
                .toMap()

        return Platform.all().map {
            val clientBind = clientBinds[it]

            if (clientBind != null) {
                PlatformVo(id = clientBind.id, platform = clientBind.platform, status = clientBind.status, open = true)
            } else {
                PlatformVo(id = -1, platform = it, status = Status.Stop, open = false)
            }
        }
    }

    @GetMapping("/open")
    override fun openList(): List<PlatformVo> {
        val clientId = getClientId()
        val clientBinds = platformBindService.findClientPlatforms(clientId)

        return clientBinds.map {
            PlatformVo(id = it.id, platform = it.platform, status = it.status, open = true)
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
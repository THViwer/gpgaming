package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.PlatformBindUo
import com.onepiece.treasure.beans.value.internet.web.PlatformUoReq
import com.onepiece.treasure.beans.value.internet.web.PlatformVo
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/platform")
class ClientPlatformApiController(): BasicController(), ClientPlatformApi {

    @GetMapping
    override fun all(): List<PlatformVo> {
        val clientBinds = platformBindService.findClientPlatforms(clientId)
                .map { it.platform to it }
                .toMap()

        return Platform.all().map {
            val clientBind = clientBinds[it]

            if (clientBind != null) {
                PlatformVo(id = clientBind.id, category = it.detail.category, name = it.detail.name, status = clientBind.status, open = true)
            } else {
                PlatformVo(id = -1, category = it.detail.category, name = it.detail.name, status = Status.Stop, open = false)
            }
        }

    }

    @PutMapping
    override fun update(@RequestBody platformUoReq: PlatformUoReq) {

        val platform = platformBindService.findClientPlatforms(clientId).find { it.id == platformUoReq.id }
        checkNotNull(platform) { OnePieceExceptionCode.AUTHORITY_FAIL }

        val platformBindUo = PlatformBindUo(id = platformUoReq.id, status = platformUoReq.status, earnestBalance = null, username = null, password = null, tokenJson = null)
        platformBindService.update(platformBindUo)
    }
}
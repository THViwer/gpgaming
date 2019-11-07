package com.onepiece.treasure.su.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.value.database.PlatformBindCo
import com.onepiece.treasure.beans.value.database.PlatformBindUo
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.su.controller.value.PlatformBindSuValue
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/client/platform")
class PlatformBindApiController(
        private val platformBindService: PlatformBindService
) : PlatformBindApi {


    @PostMapping
    override fun create(@RequestBody platformBindCoReq: PlatformBindSuValue.PlatformBindCoReq) {
        val platformBindCo = PlatformBindCo(clientId = platformBindCoReq.clientId, username = platformBindCoReq.username,
                password = platformBindCoReq.password, earnestBalance = platformBindCoReq.earnestBalance, platform = platformBindCoReq.platform)
        platformBindService.create(platformBindCo)
    }

    @PutMapping
    override fun update(@RequestBody platformBindUoReq: PlatformBindSuValue.PlatformBindUoReq) {
        val platformBindUo = PlatformBindUo(id = platformBindUoReq.id, username = platformBindUoReq.username, password = platformBindUoReq.password,
                earnestBalance = platformBindUoReq.earnestBalance, status = platformBindUoReq.status)
        platformBindService.update(platformBindUo)

    }

    @GetMapping("/{clientId}")
    override fun clientPlatform(@PathVariable("clientId") clientId: Int): List<PlatformBindSuValue.PlatformBindVo> {

        val bindMap = platformBindService.findClientPlatforms(clientId).map { it.platform to it }.toMap()

        return Platform.all().map { platform ->
            bindMap[platform]?.let {
                PlatformBindSuValue.PlatformBindVo(platform = platform, backUrl = "-", clientId = clientId, earnestBalance = it.earnestBalance,
                        username = it.username, password = it.password, open = it.status == Status.Normal)
            }?:PlatformBindSuValue.PlatformBindVo(platform = platform, backUrl = "-", clientId = clientId, earnestBalance = BigDecimal.valueOf(-1),
                    username = "-", password = "-", open = false)

        }

    }
}
package com.onepiece.gpgaming.su.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.PlatformBind
import com.onepiece.gpgaming.beans.value.database.PlatformBindCo
import com.onepiece.gpgaming.beans.value.database.PlatformBindUo
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.su.controller.value.PlatformBindSuValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/client/platform")
class PlatformBindApiController(
        private val platformBindService: PlatformBindService,
        private val objectMapper: ObjectMapper
) : PlatformBindApi {


    @PostMapping
    override fun create(@RequestBody platformBindCoReq: PlatformBindSuValue.PlatformBindCoReq) {

        this.checkClientToken(platform = platformBindCoReq.platform, tokenJson = platformBindCoReq.tokenJson)

        val platformBindCo = PlatformBindCo(clientId = platformBindCoReq.clientId, username = platformBindCoReq.username,
                password = platformBindCoReq.password, earnestBalance = platformBindCoReq.earnestBalance, platform = platformBindCoReq.platform, tokenJson = platformBindCoReq.tokenJson)
        platformBindService.create(platformBindCo)
    }

    @PutMapping
    override fun update(@RequestBody platformBindUoReq: PlatformBindSuValue.PlatformBindUoReq) {
        val platformBindUo = PlatformBindUo(id = platformBindUoReq.id, username = platformBindUoReq.username, password = platformBindUoReq.password,
                earnestBalance = platformBindUoReq.earnestBalance, status = platformBindUoReq.status, tokenJson = platformBindUoReq.tokenJson)
        platformBindService.update(platformBindUo)

    }

    private fun checkClientToken(platform: Platform, tokenJson: String) {
        val clz = PlatformBind.getClientTokenClass(platform)
        objectMapper.readValue(tokenJson, clz)
    }

    @GetMapping("/{clientId}")
    override fun clientPlatform(@PathVariable("clientId") clientId: Int): List<PlatformBindSuValue.PlatformBindVo> {

        val bindMap = platformBindService.findClientPlatforms(clientId).map { it.platform to it }.toMap()

        return Platform.all().map { platform ->
            bindMap[platform]?.let {
                PlatformBindSuValue.PlatformBindVo(platform = platform, backUrl = "-", clientId = clientId, earnestBalance = it.earnestBalance,
                        username = it.username, password = it.password, open = it.status == Status.Normal, tokenJson = objectMapper.writeValueAsString(it.clientToken),
                        id = it.id)
            }?:PlatformBindSuValue.PlatformBindVo(platform = platform, backUrl = "-", clientId = clientId, earnestBalance = BigDecimal.valueOf(-1),
                    username = "-", password = "-", open = false, tokenJson = "-", id = -1)
        }
    }
}
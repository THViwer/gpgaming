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
import org.springframework.web.bind.annotation.RequestParam
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

        val platformBindCo = with(platformBindCoReq) {
            PlatformBindCo(clientId = clientId, username = username, password = password, earnestBalance = earnestBalance, platform = platform,
                    tokenJson = tokenJson, name = name, icon = icon, disableIcon = disableIcon, originIcon = originIcon, originIconOver = originIconOver,
                    mobileIcon = mobileIcon, mobileDisableIcon = mobileDisableIcon, platformDetailIcon = platformDetailIcon, platformDetailIconOver = platformDetailIconOver,
                    unclejayMobleIcon  = unclejayMobleIcon )
        }
        platformBindService.create(platformBindCo = platformBindCo)
    }

    @PutMapping
    override fun update(@RequestBody platformBindUoReq: PlatformBindSuValue.PlatformBindUoReq) {

        val platformBindCo = with(platformBindUoReq) {
            PlatformBindUo(id = id, username = username, password = password, earnestBalance = earnestBalance, tokenJson = tokenJson, name = name, icon = icon,
                    disableIcon = disableIcon, originIcon = originIcon, originIconOver = originIconOver, mobileIcon = mobileIcon, mobileDisableIcon = mobileDisableIcon,
                    platformDetailIcon = platformDetailIcon, platformDetailIconOver = platformDetailIconOver, hot = null, new = null, status = status,
                    unclejayMobileIcon  = unclejayMobleIcon )
        }

        platformBindService.update(platformBindCo)

    }

    @GetMapping("/default/logo")
    override fun getDefaultLogo(@RequestParam("platform") platform: Platform): PlatformBindSuValue.DefaultLogo {


        val platformBind = try {
            platformBindService.find(clientId = 1, platform = platform)
        } catch (e: Exception) {
            platformBindService.find(clientId = 10004, platform = platform)
        }

        return with(platformBind) {
            PlatformBindSuValue.DefaultLogo(icon = icon, disableIcon = disableIcon, originIcon = originIcon, originIconOver = originIconOver, mobileIcon = mobileIcon,
                    mobileDisableIcon = mobileDisableIcon, platformDetailIcon = platformDetailIcon, platformDetailIconOver = platformDetailIconOver)
        }
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
                        id = it.id, status = it.status, name = it.name, icon = it.icon, disableIcon = it.disableIcon, originIcon = it.originIcon, originIconOver = it.originIconOver,
                        platformDetailIcon = it.platformDetailIcon, platformDetailIconOver = it.platformDetailIconOver, mobileIcon = it.mobileIcon, mobileDisableIcon = it.mobileDisableIcon)
            } ?: PlatformBindSuValue.PlatformBindVo(platform = platform, backUrl = "-", clientId = clientId, earnestBalance = BigDecimal.valueOf(-1),
                    username = "-", password = "-", open = false, tokenJson = "-", id = -1, status = Status.Stop, name = "", icon = "", disableIcon = "",
                    originIconOver = "", originIcon = "", mobileDisableIcon = "", mobileIcon = "", platformDetailIconOver = "", platformDetailIcon = "")
        }
    }
}
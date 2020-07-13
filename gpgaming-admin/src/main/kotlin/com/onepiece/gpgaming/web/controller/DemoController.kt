package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.value.database.PlatformBindUo
import com.onepiece.gpgaming.core.service.GamePlatformService
import com.onepiece.gpgaming.core.service.PlatformBindService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/demo")
class DemoController(
        private val gamePlatformService: GamePlatformService,
        private val platformBindService: PlatformBindService

) {

    @GetMapping("/image")
    fun asyncImage() {

        val gamePlatforms = gamePlatformService.all()
        val gamePlatformMap =  gamePlatforms.map { it.platform to it }.toMap()

        val binds = platformBindService.all()

        binds.parallelStream().forEach {
            val gamePlatform = gamePlatformMap[it.platform] ?: error("")
            val bindUo = with(gamePlatform) {
                PlatformBindUo(id = it.id, name = name, mobileIcon = mobileIcon, mobileDisableIcon = mobileDisableIcon, originIcon = originIcon,
                originIconOver = originIconOver, platformDetailIconOver = platformDetailIconOver, platformDetailIcon = platformDetailIcon,
                icon = icon, disableIcon = disableIcon)
            }
            platformBindService.update(bindUo)
        }

    }


}
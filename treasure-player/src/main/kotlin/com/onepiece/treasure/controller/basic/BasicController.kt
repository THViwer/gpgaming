package com.onepiece.treasure.controller.basic

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.internet.web.PlatformMemberVo
import com.onepiece.treasure.core.service.PlatformMemberService
import com.onepiece.treasure.games.GameApi
import org.springframework.beans.factory.annotation.Autowired

abstract class BasicController {

    @Autowired
    lateinit var platformMemberService: PlatformMemberService

    @Autowired
    lateinit var jokerGameApi: GameApi


    val clientId = 1

    val id = 1

    val ip = "192.68.2.31"

    val username = "cabbage"

    fun getPlatformMember(platform: Platform): PlatformMemberVo {
        val platforms = platformMemberService.myPlatforms(memberId = id)
        return platforms.find { platform == it.platform } ?: return this.register(platform)
    }

    fun register(platform: Platform): PlatformMemberVo {

        val platformUsername = "$clientId$id"

        val clientIdStr = when  {
            clientId < 10 -> "00$clientId"
            clientId < 100 -> "0$clientId"
            else -> "$clientId"
        }

        jokerGameApi.register("$clientIdStr$id", "123456")

        return platformMemberService.create(memberId = id, platform = platform, platformUsername = platformUsername)
    }

}
package com.onepiece.treasure.controller.basic

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.internet.web.PlatformMemberVo
import com.onepiece.treasure.core.service.PlatformMemberService

abstract class BasicController {

    lateinit var platformMemberService: PlatformMemberService

    val clientId = 1

    val id = 1

    val ip = "192.68.2.31"

    fun getPlatformMember(platform: Platform): PlatformMemberVo {
        val platforms = platformMemberService.myPlatforms(memberId = id)
        return platforms.find { platform == it.platform } ?: return this.register(platform)
    }

    fun register(platform: Platform): PlatformMemberVo {
        return platformMemberService.create(memberId = id, platform = platform)
    }

}
package com.onepiece.treasure.controller.basic

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.internet.web.PlatformMemberVo
import com.onepiece.treasure.core.service.PlatformMemberService
import com.onepiece.treasure.games.GamePlatformUtil
import com.onepiece.treasure.jwt.JwtUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest

abstract class BasicController {

    @Autowired
    lateinit var platformMemberService: PlatformMemberService

    @Autowired
    lateinit var gamePlatformUtil: GamePlatformUtil

    val ip = "192.68.2.31"

    fun current(): JwtUser {
        try {
            return SecurityContextHolder.getContext().authentication.principal as JwtUser
        } catch (e: Exception) {
            throw IllegalArgumentException("无法活的当前用户")
        }
    }

    fun currentClientIdAndMemberId():Pair<Int, Int> {
        val member = current()
        return member.clientId to member.id
    }

    fun getRequest(): HttpServletRequest {
        return (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
    }

    fun getPlatformMember(platform: Platform): PlatformMemberVo {
        val platforms = platformMemberService.myPlatforms(memberId = current().id)
        return platforms.find { platform == it.platform } ?: return this.registerPlatformMember(platform)
    }

    fun registerPlatformMember(platform: Platform): PlatformMemberVo {

        val member = current()
        val clientId = member.clientId
        val id = member.id


        val clientIdStr = when  {
            clientId < 10 -> "00$clientId"
            clientId < 100 -> "0$clientId"
            else -> "$clientId"
        }.let {
            when (platform) {
                Platform.Cta666 -> "c$it"
                else -> it
            }
        }

        val platformUsername = "$clientIdStr$id"
        val generatorUsername = gamePlatformUtil.getPlatformBuild(platform).gameApi.register(platformUsername, "123456")

        return platformMemberService.create(memberId = id, platform = platform, platformUsername = generatorUsername)
    }

}
package com.onepiece.treasure.controller.basic

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.internet.web.PlatformMemberVo
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.core.service.PlatformMemberService
import com.onepiece.treasure.games.GameApi
import com.onepiece.treasure.games.value.ClientAuthVo
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
    lateinit var platformBindService: PlatformBindService

    @Autowired
    lateinit var gameApi: GameApi

    val ip = "192.68.2.31"

    fun getClientIdByDomain(): Int {
        return 1
    }

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
        val member = current()
        val platforms = platformMemberService.myPlatforms(memberId = member.id)
        val platformMember = platforms.find { platform == it.platform }

        if (platformMember == null) {
            gameApi.register(clientId = member.clientId, memberId = member.id, platform = platform)
            return this.getPlatformMember(platform)
        }

        return platformMember
    }


    fun getClientAuthVo(platform: Platform): ClientAuthVo {
        return when (platform) {
            Platform.Kiss918 -> {
                val bind = platformBindService.findClientPlatforms(current().clientId).find { it.platform == platform }!!
                ClientAuthVo.ofKiss918(bind.username)
            }
            else -> ClientAuthVo.empty()
        }
    }


}
package com.onepiece.gpgaming.player.controller.basic

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.value.internet.web.PlatformMemberVo
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.core.service.PlatformMemberService
import com.onepiece.gpgaming.core.service.WebSiteService
import com.onepiece.gpgaming.games.GameApi
import com.onepiece.gpgaming.games.value.ClientAuthVo
import com.onepiece.gpgaming.player.jwt.JwtUser
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest


abstract class BasicController {

    private val log = LoggerFactory.getLogger(BasicController::class.java)

    @Autowired
    lateinit var platformMemberService: PlatformMemberService

    @Autowired
    lateinit var platformBindService: PlatformBindService

    @Autowired
    lateinit var gameApi: GameApi

    @Autowired
    lateinit var webSiteService: WebSiteService

    /**
     * 获得请求ip
     */
    fun getIpAddress(): String {
        val request = this.getRequest()

        var ip = request.getHeader("x-forwarded-for")
        if (ip.isNullOrBlank() || "unknown" == ip.toLowerCase()) {
            ip = request.getHeader("Proxy-Client-IP")
            log.info("Proxy-Client-IP = $ip")
        }

        if (ip.isNullOrBlank() || "unknown" == ip.toLowerCase()) {
            ip = request.getHeader("WL-Proxy-Client-IP")
            log.info("WL-Proxy-Client-IP = $ip")
        }

        if (ip.isNullOrBlank() || "unknown" == ip.toLowerCase()) {
            ip = request.getHeader("HTTP_CLIENT_IP")
            log.info("HTTP_CLIENT_IP = $ip")
        }

        if (ip.isNullOrBlank() || "unknown" == ip.toLowerCase()) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
            log.info("HTTP_X_FORWARDED_FOR = $ip")
        }

        if (ip.isNullOrBlank() || "unknown" == ip.toLowerCase()) {
            ip = request.remoteAddr
            log.info("request.remoteAddr = $ip")
        }

        return ip.split(",").first()
    }

    fun getClientIdByDomain(): Int {
        val request = this.getRequest()
        val url = request.requestURL.toString()
        return webSiteService.match(url)
    }

    fun current(): JwtUser {
        try {
            return SecurityContextHolder.getContext().authentication.principal as JwtUser
        } catch (e: Exception) {
            error("无法获得当前用户")
        }
    }

    fun currentClientIdAndMemberId():Pair<Int, Int> {
        val member = current()
        return member.clientId to member.id
    }

    fun getRequest(): HttpServletRequest {
        return (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
    }

    @Synchronized
    fun getPlatformMember(platform: Platform, member: JwtUser): PlatformMemberVo {
        val platforms = platformMemberService.myPlatforms(memberId = member.id)
        val platformMember = platforms.find { platform == it.platform }

        if (platformMember == null) {
            gameApi.register(clientId = member.clientId, memberId = member.id, platform = platform, name = member.musername)
            return this.getPlatformMember(platform, member)
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

    fun currentUsername(): String {
        return this.current().username.split("@")[1]
    }

}
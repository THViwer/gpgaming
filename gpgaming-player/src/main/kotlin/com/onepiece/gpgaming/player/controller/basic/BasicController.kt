package com.onepiece.gpgaming.player.controller.basic

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.value.internet.web.PlatformMemberVo
import com.onepiece.gpgaming.core.service.GamePlatformService
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.core.service.PlatformMemberService
import com.onepiece.gpgaming.core.service.WebSiteService
import com.onepiece.gpgaming.games.GameApi
import com.onepiece.gpgaming.games.value.ClientAuthVo
import com.onepiece.gpgaming.player.jwt.JwtUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    @Autowired
    lateinit var gamePlatformService: GamePlatformService

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
    fun getPlatformMember(platform: Platform, member: JwtUser, code: Int = 0): PlatformMemberVo {
        val platforms = platformMemberService.myPlatforms(memberId = member.id)
        val platformMember = platforms.find { platform == it.platform }

        // 判断平台是否维护
        check(gamePlatformService.all().first { it.platform == platform }.status == Status.Normal) { OnePieceExceptionCode.PLATFORM_MAINTAIN }


        if (code > 3) error(OnePieceExceptionCode.SYSTEM)

        log.info("用户名：${member.username}, 获得平台用户：$platform, code = $code")
        if (platformMember == null) {
            log.info("用户名：${member.username}, 开始注册平台用户：$platform, code = $code" + "")


//            gameApi(clientId = member.clientId, memberId = member.id, platform = platform, name = member.musername)
            gameApi.register(clientId = member.clientId, memberId = member.id, platform = platform, name = member.username)

            return this.getPlatformMember(platform, member, code + 1)
        }

        return platformMember
    }

    fun startRegister(clientId: Int, memberId: Int, platform: Platform, name: String) = runBlocking {
        val x = launch {
            gameApi.register(clientId = clientId, memberId = memberId, platform = platform, name = name)
        }
        x.join()
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
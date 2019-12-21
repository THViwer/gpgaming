package com.onepiece.gpgaming.web.controller.basic

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.games.value.ClientAuthVo
import com.onepiece.gpgaming.web.jwt.JwtUser
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest

abstract class BasicController {

    private val log = LoggerFactory.getLogger(BasicController::class.java)

    @Autowired
    lateinit var platformBindService: PlatformBindService

//    val id = 1
//
//    val clientId = 1
//
//    val waiterId: Int
//        get() {
//            return if (role == Role.Client) -1 else id
//        }
//
//    val role = Role.Waiter
//
//    val name = "zhangdan"

    fun current(): JwtUser {
        try {
            return SecurityContextHolder.getContext().authentication.principal as JwtUser
        } catch (e: Exception) {
            throw IllegalArgumentException("无法获得当前用户")
        }
    }

    fun getClientId() = current().clientId

    fun getCurrentWaiterId(): Int? {
        return if (this.current().role == Role.Client) null else this.current().id
    }


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

        return ip
    }

    fun getRequest(): HttpServletRequest {
        return (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
    }


    fun getClientAuthVo(platform: Platform): ClientAuthVo {
        return when (platform) {
            Platform.Kiss918 -> {
                val bind = platformBindService.findClientPlatforms(getClientId()).find { it.platform == platform }!!
                ClientAuthVo.ofKiss918(bind.username)
            }
            else -> ClientAuthVo.empty()
        }
    }

}
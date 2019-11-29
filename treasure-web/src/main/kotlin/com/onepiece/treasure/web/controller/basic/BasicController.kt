package com.onepiece.treasure.web.controller.basic

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Role
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.games.value.ClientAuthVo
import com.onepiece.treasure.web.jwt.JwtUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest

abstract class BasicController {

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


    /**
     * 获得请求ip
     */
    fun getIpAddress(): String {
        val request = this.getRequest()

        var ip = request.getHeader("x-forwarded-for")
        if (ip.isNullOrBlank() || "unknown" == ip?.toLowerCase()) {
            ip = request.getHeader("Proxy-Client-IP")
        }

        if (ip.isNullOrBlank() || "unknown" == ip?.toLowerCase()) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }

        if (ip.isNullOrBlank() || "unknown" == ip?.toLowerCase()) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }

        if (ip.isNullOrBlank() || "unknown" == ip?.toLowerCase()) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }

        if (ip.isNullOrBlank() || "unknown" == ip?.toLowerCase()) {
            ip = request.remoteAddr;
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
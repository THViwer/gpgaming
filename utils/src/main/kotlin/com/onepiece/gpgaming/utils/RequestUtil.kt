package com.onepiece.gpgaming.utils

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest

object RequestUtil {

    private val log = Logger.getLogger("RequestUtil")

    fun getRequest(): HttpServletRequest {
        return (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
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

        return ip.split(",").first()
    }

}
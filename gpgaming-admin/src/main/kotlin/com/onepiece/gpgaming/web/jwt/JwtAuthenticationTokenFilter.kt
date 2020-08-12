package com.onepiece.gpgaming.web.jwt

import com.onepiece.gpgaming.beans.value.database.LoginHistoryValue
import com.onepiece.gpgaming.core.service.LoginHistoryService
import com.onepiece.gpgaming.utils.RequestUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.Exception
import java.time.LocalDate
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationTokenFilter(
//        val userDetailsService: UserDetailsService,
        private val jwtTokenUtil: JwtTokenUtil,
        private val tokenStore: TokenStore,
        private val loginHistoryService: LoginHistoryService
) : OncePerRequestFilter() {

    @Value("\${jwt.header}")
    lateinit var tokenHeader: String

    @Value("\${jwt.tokenHead}")
    lateinit var tokenHead: String


    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authHeader = request.getHeader(this.tokenHeader)
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            val authToken = authHeader.substring(tokenHead.length) // The part after "Bearer "
            if (SecurityContextHolder.getContext().authentication == null) {

                val userDetails = tokenStore.readAccessToken(authToken)

                if (userDetails != null && jwtTokenUtil.validateToken(authToken, userDetails)) {

                    try {
                        this.refreshToken(userDetails)
                    } catch (e: Exception)  {

                    }

                    val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
//                    logger.info("authenticated user $authToken, setting security context")
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }

        chain.doFilter(request, response)
    }


    private fun refreshToken(user: JwtUser) {

        val redisKey = "tokenToLogin:${LocalDate.now()}:${user.id}"
        val isExist = tokenStore.redisService.get(redisKey, Int::class.java)

        if (isExist == null) {
            val co = LoginHistoryValue.LoginHistoryCo(bossId = user.bossId, clientId = user.clientId, userId = user.id, role = user.role, ip = RequestUtil.getIpAddress(),
            username = user.username.split("@").last(),  deviceType = "pc")
            loginHistoryService.create(co)

            tokenStore.redisService.put(key = redisKey, value = 1, timeout = 86400)
        }
    }


}
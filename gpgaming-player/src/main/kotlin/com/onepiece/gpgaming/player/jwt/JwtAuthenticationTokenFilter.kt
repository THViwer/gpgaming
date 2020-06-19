package com.onepiece.gpgaming.player.jwt

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationTokenFilter(
//        val userDetailsService: UserDetailsService,
        private val jwtTokenUtil: JwtTokenUtil,
        private val tokenStore: TokenStore,
        private val passwordEncoder: PasswordEncoder
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtAuthenticationTokenFilter::class.java)

    @Value("\${jwt.header}")
    lateinit var tokenHeader: String

    @Value("\${jwt.tokenHead}")
    lateinit var tokenHead: String

    @Value("\${jwt.hashSecret}")
    lateinit var hashSecret: String


    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authHeader = request.getHeader(this.tokenHeader)
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            val authToken = authHeader.substring(tokenHead.length) // The part after "Bearer "
            if (SecurityContextHolder.getContext().authentication == null) {

                val userDetails = tokenStore.readAccessToken(authToken)

                if (userDetails != null && jwtTokenUtil.validateToken(authToken, userDetails)) {
                    val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
//                    logger.info("authenticated user $authToken, setting security context")
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }

        this.validHash(request = request)

        chain.doFilter(request, response)
    }

    private fun validHash(request: HttpServletRequest) {

        val requestURL = request.requestURI
        if (requestURL == "/api/v1/player/" || requestURL.contains("/swagger") || requestURL.contains("/webjars") || requestURL.contains("/api-docs")) {
            return
        }

        val otp = request.getHeader("opt")
        val hash = request.getHeader("hash")


        val username = SecurityContextHolder.getContext()?.authentication?.details?.let { it as JwtUser }?.username

        val param = listOfNotNull(
                username,
                hashSecret,
                otp
        ).joinToString(separator = ":")

        val valid = passwordEncoder.matches(param, hash)
//        check(valid) { HttpStatus.UNAUTHORIZED }
        if (!valid) {
            log.info("------------------------")
            log.info("------------------------")
            log.info("------------------------")
            log.info("------------------------")
            log.info("------------------------")
            log.info("验证失败，字符串=$param,otp=$otp,上传密钥：$hash, 不匹配")
            log.info("------------------------")
            log.info("------------------------")
            log.info("------------------------")
            log.info("------------------------")
            log.info("------------------------")
        }
    }
}
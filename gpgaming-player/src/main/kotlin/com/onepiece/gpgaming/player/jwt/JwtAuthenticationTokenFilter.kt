package com.onepiece.gpgaming.player.jwt

import com.onepiece.gpgaming.beans.value.database.MemberInfoValue
import com.onepiece.gpgaming.core.risk.VipUtil
import com.onepiece.gpgaming.core.service.MemberInfoService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.time.LocalDate
import java.time.ZoneId
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

open class JwtAuthenticationTokenFilter(
//        val userDetailsService: UserDetailsService,
        private val jwtTokenUtil: JwtTokenUtil,
        private val tokenStore: TokenStore,
        private val passwordEncoder: PasswordEncoder,
        private val memberInfoService: MemberInfoService,
        private val vipUtil: VipUtil
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

        var username: String? = null
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            val authToken = authHeader.substring(tokenHead.length) // The part after "Bearer "
            if (SecurityContextHolder.getContext().authentication == null) {

                val userDetails = tokenStore.readAccessToken(authToken)
                username = userDetails?.musername?.split("@")?.last()

                if (userDetails != null && jwtTokenUtil.validateToken(authToken, userDetails)) {
                    val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
//                    logger.info("authenticated user $authToken, setting security context")
                    SecurityContextHolder.getContext().authentication = authentication

                    this.refreshToken(user = userDetails)
                }
            }
        }

        this.validHash(request = request, username = username)

        chain.doFilter(request, response)
    }

//    @Async
    open fun refreshToken(user: JwtUser) {

        val produceDate = user.produceDate

        val localDate = produceDate.toInstant().atZone(ZoneId.of("Asia/Shanghai")).toLocalDate()

        val redisKey = "tokenToLogin:${LocalDate.now()}:${user.id}"
        val isExist = tokenStore.redisService.get(redisKey, Int::class.java)

        if (localDate != LocalDate.now() && isExist != null) {
            val infoUo = MemberInfoValue.MemberInfoUo.ofLogin(memberId = user.id)
            memberInfoService.asyncUpdate(uo = infoUo)

            tokenStore.redisService.put(key = redisKey, value = 1, timeout = 86400)

            // 检查vip等级
            vipUtil.checkAndUpdateVip(clientId = user.clientId, memberId = user.id)
        }
    }

    private fun validHash(request: HttpServletRequest, username: String?) {

        val requestURL = request.requestURI
        if (requestURL.contains("/api/v1/player/") || requestURL.contains("/swagger") || requestURL.contains("/webjars") || requestURL.contains("/api-docs")) {
            return
        }

        val otp = request.getHeader("opt")
        val hash = request.getHeader("hash")


        val param = listOfNotNull(
                username,
                hashSecret,
                otp
        ).joinToString(separator = "")

        val valid = passwordEncoder.matches(param, hash)

        log.info("验证hash，字符串=$param , otp=$otp, username = $username, 上传密钥：$hash")

        check(valid) { HttpStatus.UNAUTHORIZED }
//        if (!valid) {
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("验证失败，字符串=$param,otp=$otp,上传密钥：$hash, 不匹配")
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("------------------------")
//        }
    }
}
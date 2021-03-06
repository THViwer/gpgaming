package com.onepiece.gpgaming.player.jwt

import com.onepiece.gpgaming.beans.value.database.LoginHistoryValue
import com.onepiece.gpgaming.beans.value.database.MemberInfoValue
import com.onepiece.gpgaming.core.risk.VipUtil
import com.onepiece.gpgaming.core.service.LoginHistoryService
import com.onepiece.gpgaming.core.service.MemberInfoService
import com.onepiece.gpgaming.utils.RequestUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.Exception
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
        private val loginHistoryService: LoginHistoryService,
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

            tokenStore.redisService.put(key = redisKey, value = 1, timeout = 86400)

            // ??????????????????
            val infoUo = MemberInfoValue.MemberInfoUo.ofLogin(memberId = user.id)
            memberInfoService.asyncUpdate(uo = infoUo)

            try {
                val co = LoginHistoryValue.LoginHistoryCo(bossId = user.bossId, clientId = user.clientId, userId = user.id, role = user.role, ip = RequestUtil.getIpAddress(),
                        username = user.username.split("@").last(), deviceType = "pc")
                loginHistoryService.create(co)
            } catch (e: Exception) {

            }


            // ??????vip??????
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

        log.info("??????hash????????????=$param , otp=$otp, username = $username, ???????????????$hash")

        check(valid) { HttpStatus.UNAUTHORIZED }
//        if (!valid) {
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("????????????????????????=$param,otp=$otp,???????????????$hash, ?????????")
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("------------------------")
//            log.info("------------------------")
//        }
    }
}
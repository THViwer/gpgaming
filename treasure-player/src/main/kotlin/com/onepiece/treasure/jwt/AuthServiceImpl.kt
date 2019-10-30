package com.onepiece.treasure.jwt

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.*

interface AuthService {

    fun  login(username: String): String

    fun refresh(id: Int)

}

@Service
class AuthServiceImpl(
        private val authenticationManager: AuthenticationManager,
        private val jwtUserDetailsServiceImpl: UserDetailsService,
        private val jwtTokenUtil: JwtTokenUtil,
        private val tokenStore: TokenStore
) : AuthService {

    override fun login(username: String): String {
        val upToken = UsernamePasswordAuthenticationToken(username, username)
        val authentication = authenticationManager.authenticate(upToken)
        SecurityContextHolder.getContext().authentication = authentication

        // Reload password post-security so we can generate token
        val jwtUser = jwtUserDetailsServiceImpl.loadUserByUsername(username) as JwtUser
        val token = jwtTokenUtil.generateToken(jwtUser)

        tokenStore.storeAccessToken(username = username, token = token, jwtUser = jwtUser)

//        return MobileUser(userId = jwtUser.id, token = token)
        return token
    }


    override fun refresh(id: Int) {

//        val user = userService.get(id)
//
//        val username = user.name
//        val password = user.name

        val jwtUser = JwtUser(id = 1, clientId = 1, musername = "", mpassword = "", lastPasswordResetDate = Date())
        tokenStore.refresh(jwtUser)
    }
}

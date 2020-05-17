package com.onepiece.gpgaming.player.jwt

import com.onepiece.gpgaming.beans.enums.Role
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

interface AuthService {

    fun  login(clientId: Int, role: Role, username: String): String

    fun refresh(id: Int)

}

@Service
class AuthServiceImpl(
        private val authenticationManager: AuthenticationManager,
        private val jwtUserDetailsServiceImpl: UserDetailsService,
        private val jwtTokenUtil: JwtTokenUtil,
        private val tokenStore: TokenStore
) : AuthService {

    override fun login(clientId: Int, role: Role, username: String): String {
        val upToken = UsernamePasswordAuthenticationToken("${clientId}@${role}@${username}", "123456")
        val authentication = authenticationManager.authenticate(upToken)
        SecurityContextHolder.getContext().authentication = authentication

        // Reload password post-security so we can generate token
        val jwtUser = authentication.principal as JwtUser
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

//        val jwtUser = JwtUser(id = 1, clientId = 1, musername = "", mpassword = "", lastPasswordResetDate = Date())
//        tokenStore.refresh(jwtUser)
    }
}

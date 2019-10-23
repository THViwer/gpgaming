package com.onepiece.treasure.jwt

import com.onepiece.treasure.core.service.UserService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.*

interface AuthService {

    fun login(id: Int): MobileUser

    fun refresh(id: Int)

}

@Service
class AuthServiceImpl(
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService,
        private val jwtTokenUtil: JwtTokenUtil,
        private val tokenStore: TokenStore,
        private val userService: UserService
) : AuthService {

    override fun login(id: Int): MobileUser {


        val user = userService.get(id)

        val username = user.name
        val password = user.name

        val upToken = UsernamePasswordAuthenticationToken(username, username)
        val authentication = authenticationManager.authenticate(upToken)
        SecurityContextHolder.getContext().authentication = authentication

        // Reload password post-security so we can generate token
        val userDetails = userDetailsService.loadUserByUsername(username)
        val token = jwtTokenUtil.generateToken(userDetails)

        val jwtUser = JwtUser(id = user.id, musername = username, mpassword = password, lastPasswordResetDate = Date())
        tokenStore.storeAccessToken(username = username, token = token, jwtUser = jwtUser)

        return MobileUser(userId = user.id, token = token)
    }


    override fun refresh(id: Int) {

        val user = userService.get(id)

        val username = user.name
        val password = user.name

        val jwtUser = JwtUser(id = user.id, musername = username, mpassword = password, lastPasswordResetDate = Date())
        tokenStore.refresh(jwtUser)
    }
}

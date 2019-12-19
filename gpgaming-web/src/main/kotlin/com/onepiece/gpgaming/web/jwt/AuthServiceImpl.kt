package com.onepiece.gpgaming.web.jwt

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.WaiterService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.*

interface AuthService {

    fun login(id: Int, role: Role, username: String, mAuthorities: List<String>): MobileUser

    fun refresh(id: Int)

}

@Service
class AuthServiceImpl(
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService,
        private val jwtTokenUtil: JwtTokenUtil,
        private val tokenStore: TokenStore,
        private val clientService: ClientService,
        private val waiterService: WaiterService
//        private val userService: UserService
) : AuthService {

    override fun login(id: Int, role: Role, username: String, mAuthorities: List<String>): MobileUser {
        val password = "123456"

        val clientId = when (role) {
            Role.Client -> id
            Role.Waiter -> {
                val waiter = waiterService.get(id)
                waiter.clientId
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

        val authUsername = "$clientId:$id:$username:$role"

        val upToken = UsernamePasswordAuthenticationToken(authUsername, username)
        val authentication = authenticationManager.authenticate(upToken)
        SecurityContextHolder.getContext().authentication = authentication

        val userDetails = userDetailsService.loadUserByUsername(authUsername) as JwtUser
        val token = jwtTokenUtil.generateToken(userDetails)
        tokenStore.storeAccessToken(username = username, token = token, jwtUser = userDetails)


        val jwtUser = JwtUser(id = id, musername = username, mpassword = password, lastPasswordResetDate = Date(),
                clientId = clientId, role = role, mauthorities = mAuthorities)
        tokenStore.storeAccessToken(username = username, token = token, jwtUser = jwtUser)

        return MobileUser(userId = 1, token = token)
    }


    override fun refresh(id: Int) {

//        val user = userService.get(id)
//
//        val username = user.name
//        val password = user.name

//        val jwtUser = JwtUser(id = 1, musername = "admin", mpassword = "admin", lastPasswordResetDate = Date())
//        tokenStore.refresh(jwtUser)
    }
}

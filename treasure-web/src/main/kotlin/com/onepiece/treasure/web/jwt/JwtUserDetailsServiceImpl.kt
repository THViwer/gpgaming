package com.onepiece.treasure.web.jwt

import com.onepiece.treasure.beans.enums.Role
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtUserDetailsServiceImpl(
        private val passwordEncoder: PasswordEncoder
): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {

        val (clientId, currentUserId, mUsername, role) = username.split(":")
//        val user = userDao.getByUsername(username)!!

        return JwtUser(id = currentUserId.toInt(), musername = mUsername, mpassword = passwordEncoder.encode(mUsername),
                lastPasswordResetDate = Date(), clientId = clientId.toInt(), role = Role.valueOf(role)
        )
    }
}
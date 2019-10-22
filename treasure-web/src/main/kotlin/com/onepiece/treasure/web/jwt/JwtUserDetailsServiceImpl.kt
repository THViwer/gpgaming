package com.onepiece.treasure.web.jwt

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

//        val user = userDao.getByUsername(username)!!

        return JwtUser(id = 1, musername = username, mpassword = passwordEncoder.encode(username),
                lastPasswordResetDate = Date()
        )
    }
}
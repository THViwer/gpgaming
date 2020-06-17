package com.onepiece.gpgaming.player.jwt

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.core.dao.MemberDao
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtUserDetailsServiceImpl(
        private val passwordEncoder: PasswordEncoder,
        private val memberDao: MemberDao
): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {

//        val user = userDao.getByUsername(username)!!

        val (bossId, clientId, role, platformUsername) = username.split("@")
        val member = memberDao.getByUsername(clientId.toInt(), platformUsername)!!

        return JwtUser(bossId = bossId.toInt(), clientId = member.clientId, id = member.id, musername = username, mpassword = passwordEncoder.encode("123456"),
                lastPasswordResetDate = Date(), name = member.name, role = Role.valueOf(role))
    }
}
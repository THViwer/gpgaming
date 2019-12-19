package com.onepiece.gpgaming.player.jwt

import com.onepiece.gpgaming.core.dao.MemberDao
import com.onepiece.gpgaming.core.service.PlatformMemberService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtUserDetailsServiceImpl(
        private val passwordEncoder: PasswordEncoder,
        private val memberDao: MemberDao,
        private val platformMemberService: PlatformMemberService
): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {

//        val user = userDao.getByUsername(username)!!

        val member = memberDao.getByUsername(username)!!

        return JwtUser(clientId = member.clientId, id = member.id, musername = username, mpassword = passwordEncoder.encode(username),
                lastPasswordResetDate = Date(), name = member.name)
    }
}
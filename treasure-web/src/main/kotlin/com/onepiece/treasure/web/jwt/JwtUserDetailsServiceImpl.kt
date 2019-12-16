package com.onepiece.treasure.web.jwt

import com.onepiece.treasure.beans.enums.PermissionType
import com.onepiece.treasure.beans.enums.Role
import com.onepiece.treasure.core.service.PermissionService
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtUserDetailsServiceImpl(
        private val passwordEncoder: PasswordEncoder,
        private val permissionService: PermissionService
): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {

        val (clientId, currentUserId, mUsername, role) = username.split(":")
//        val user = userDao.getByUsername(username)!!

        val permissions = if (role == Role.Admin.name) {
            PermissionType.values().map { it.resourceId }
        } else {
            permissionService.findWaiterPermissions(currentUserId.toInt()).permissions.filter { it.effective }.map { it.resourceId }.plus("-1")
        }

        return JwtUser(id = currentUserId.toInt(), musername = mUsername, mpassword = passwordEncoder.encode(mUsername),
                lastPasswordResetDate = Date(), clientId = clientId.toInt(), role = Role.valueOf(role), mAuthorities = permissions
        )
    }
}
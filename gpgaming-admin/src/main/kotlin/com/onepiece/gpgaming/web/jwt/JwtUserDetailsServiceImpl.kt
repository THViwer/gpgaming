package com.onepiece.gpgaming.web.jwt

import com.onepiece.gpgaming.beans.enums.PermissionType
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.PermissionService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtUserDetailsServiceImpl(
        private val passwordEncoder: PasswordEncoder,
        private val permissionService: PermissionService,
        private val clientService: ClientService
): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {

        val (bossId, clientId, currentUserId, mUsername, role) = username.split(":")

        val mainClient = clientService.getMainClient(bossId = bossId.toInt())!!

        val defaultPermissions = PermissionType.values()
        val permissions = when {
            mUsername == "super_admin" -> defaultPermissions.map { it.resourceId }
            role == Role.Admin.name -> defaultPermissions.filter { it.resourceId != PermissionType.CASH_THIRD_PAY_SETTING.resourceId }.map { it.resourceId }
            role == Role.Waiter.name -> {
                permissionService.findWaiterPermissions(currentUserId.toInt())
                        .permissions
                        .filter { it.effective }.map { it.resourceId }
                        .plus("-1")
                        .filter { it != PermissionType.SALE_MANAGER.resourceId }
            }
            role == Role.Sale.name -> {
                listOf(PermissionType.SALE_MANAGER.resourceId)
            }
            else -> error("401")
        }

        return JwtUser(id = currentUserId.toInt(), musername = mUsername, mpassword = passwordEncoder.encode(mUsername),
                lastPasswordResetDate = Date(), clientId = clientId.toInt(), role = Role.valueOf(role), mauthorities = permissions,
                bossId = bossId.toInt()
        )
    }

}
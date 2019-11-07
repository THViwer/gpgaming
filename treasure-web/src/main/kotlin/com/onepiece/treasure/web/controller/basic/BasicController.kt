package com.onepiece.treasure.web.controller.basic

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Role
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.games.value.ClientAuthVo
import org.springframework.beans.factory.annotation.Autowired

abstract class BasicController {

    @Autowired
    lateinit var platformBindService: PlatformBindService

    val id = 1

    val clientId = 1

    val waiterId: Int
        get() {
            return if (role == Role.Client) -1 else id
        }

    val currentIp = "192.168.2.1"

    val role = Role.Waiter

    val name = "zhangdan"

    fun getClientAuthVo(platform: Platform): ClientAuthVo {
        return when (platform) {
            Platform.Kiss918 -> {
                val bind = platformBindService.findClientPlatforms(clientId).find { it.platform == platform }!!
                ClientAuthVo.ofKiss918(bind.username!!)
            }
            else -> ClientAuthVo.empty()
        }
    }

}
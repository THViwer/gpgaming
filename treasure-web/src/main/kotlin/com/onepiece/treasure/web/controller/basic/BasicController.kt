package com.onepiece.treasure.web.controller.basic

import com.onepiece.treasure.beans.enums.Role

abstract class BasicController {

    val id = 1

    val clientId = 1

    val waiterId: Int
        get() {
            return if (role == Role.Client) -1 else id
        }

    val currentIp = "192.168.2.1"

    val role = Role.Client

    val name = "zhangdan"

}
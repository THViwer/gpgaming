package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.enums.LaunchMethod

interface ComingSoonService {

    fun create(ip: String, email: String, launch: LaunchMethod)

}
package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.core.dao.ComingSoonDao
import com.onepiece.gpgaming.core.service.ComingSoonService
import org.springframework.stereotype.Service

@Service
class ComingSoonServiceImpl(
        private val comingSoonDao: ComingSoonDao
) : ComingSoonService {

    override fun create(ip: String, email: String, launch: LaunchMethod) {
        val flag = comingSoonDao.create(ip = ip, email = email, launch = launch)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}
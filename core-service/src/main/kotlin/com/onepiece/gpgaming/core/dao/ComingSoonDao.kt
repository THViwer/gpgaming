package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.model.ComingSoon
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface ComingSoonDao : BasicDao<ComingSoon> {

    fun create(ip: String, email: String, launch: LaunchMethod): Boolean

}
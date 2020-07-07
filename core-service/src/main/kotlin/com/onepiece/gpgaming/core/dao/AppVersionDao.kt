package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.AppVersion
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface AppVersionDao: BasicDao<AppVersion> {

    fun getVersions(mainClientId: Int): List<AppVersion>

}
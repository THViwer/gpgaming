package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.AppVersion
import com.onepiece.gpgaming.core.dao.AppVersionDao
import com.onepiece.gpgaming.core.service.AppVersionService
import org.springframework.stereotype.Service

@Service
class AppVersionServiceImpl(
        private val appVersionDao: AppVersionDao
): AppVersionService {

    override fun getVersions(mainClientId: Int): List<AppVersion> {
        return appVersionDao.getVersions(mainClientId = mainClientId)
    }
}
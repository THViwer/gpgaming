package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.AppVersion

interface AppVersionService {

    fun getVersions(mainClientId: Int): List<AppVersion>

}
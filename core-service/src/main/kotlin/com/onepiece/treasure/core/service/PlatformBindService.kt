package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.PlatformBind
import com.onepiece.treasure.beans.value.database.PlatformBindCo
import com.onepiece.treasure.beans.value.database.PlatformBindUo

interface PlatformBindService {

    fun findClientPlatforms(clientId: Int): List<PlatformBind>

    fun create(platformBindCo: PlatformBindCo)

    fun update(platformBindUo: PlatformBindUo)

}
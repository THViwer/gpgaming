package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.PlatformBind
import com.onepiece.gpgaming.beans.value.database.PlatformBindCo
import com.onepiece.gpgaming.beans.value.database.PlatformBindUo
import java.math.BigDecimal

interface PlatformBindService {

    fun all(): List<PlatformBind>

    fun find(platform: Platform): List<PlatformBind>

    fun find(clientId: Int, platform: Platform): PlatformBind

    fun findClientPlatforms(clientId: Int): List<PlatformBind>

    fun create(platformBindCo: PlatformBindCo)

    fun update(platformBindUo: PlatformBindUo)

    fun updateEarnestBalance(clientId: Int, platform: Platform, earnestBalance: BigDecimal)

}
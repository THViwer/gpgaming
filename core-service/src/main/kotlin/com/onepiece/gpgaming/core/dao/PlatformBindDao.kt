package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.PlatformBind
import com.onepiece.gpgaming.beans.value.database.PlatformBindCo
import com.onepiece.gpgaming.beans.value.database.PlatformBindUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao
import java.math.BigDecimal

interface PlatformBindDao : BasicDao<PlatformBind> {

    fun find(platform: Platform): List<PlatformBind>

    fun find(clientId: Int, platform: Platform): PlatformBind

    fun create(platformBindCo: PlatformBindCo): Boolean

    fun update(platformBindUo: PlatformBindUo): Boolean

    fun updateEarnestBalance(id: Int, earnestBalance: BigDecimal, processId: String): Boolean

}
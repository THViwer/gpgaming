package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.PlatformBind
import com.onepiece.treasure.beans.value.database.PlatformBindCo
import com.onepiece.treasure.beans.value.database.PlatformBindUo
import com.onepiece.treasure.core.dao.basic.BasicDao
import java.math.BigDecimal

interface PlatformBindDao : BasicDao<PlatformBind> {

    fun find(platform: Platform): List<PlatformBind>

    fun find(clientId: Int, platform: Platform): PlatformBind

    fun create(platformBindCo: PlatformBindCo): Boolean

    fun update(platformBindUo: PlatformBindUo): Boolean

    fun updateEarnestBalance(id: Int, earnestBalance: BigDecimal, processId: String): Boolean

}
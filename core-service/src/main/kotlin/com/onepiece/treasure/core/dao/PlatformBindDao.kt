package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.PlatformBind
import com.onepiece.treasure.beans.value.database.PlatformBindCo
import com.onepiece.treasure.beans.value.database.PlatformBindUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface PlatformBindDao : BasicDao<PlatformBind> {

    fun find(platform: Platform): List<PlatformBind>

    fun create(platformBindCo: PlatformBindCo): Boolean

    fun update(platformBindUo: PlatformBindUo): Boolean

}
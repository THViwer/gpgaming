package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.PlatformBindCo
import com.onepiece.treasure.core.dao.value.PlatformBindUo
import com.onepiece.treasure.core.model.PlatformBind

interface PlatformBindDao : BasicDao<PlatformBind> {

    fun create(platformBindCo: PlatformBindCo): Boolean

    fun update(platformBindUo: PlatformBindUo): Boolean

}
package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.PlatformBindCo
import com.onepiece.treasure.beans.value.database.PlatformBindUo
import com.onepiece.treasure.beans.model.PlatformBind

interface PlatformBindDao : BasicDao<PlatformBind> {

    fun create(platformBindCo: PlatformBindCo): Boolean

    fun update(platformBindUo: PlatformBindUo): Boolean

}
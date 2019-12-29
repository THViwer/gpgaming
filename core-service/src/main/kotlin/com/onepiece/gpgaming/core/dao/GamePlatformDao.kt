package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.GamePlatform
import com.onepiece.gpgaming.beans.value.database.GamePlatformValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface GamePlatformDao : BasicDao<GamePlatform> {

    fun create(co: GamePlatformValue.GamePlatformCo): Boolean

    fun update(uo: GamePlatformValue.GamePlatformUo): Boolean

}

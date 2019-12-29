package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.GamePlatform
import com.onepiece.gpgaming.beans.value.database.GamePlatformValue

interface GamePlatformService {

    fun all(): List<GamePlatform>

    fun create(gamePlatformCo: GamePlatformValue.GamePlatformCo)

    fun update(gamePlatformUo: GamePlatformValue.GamePlatformUo)


}
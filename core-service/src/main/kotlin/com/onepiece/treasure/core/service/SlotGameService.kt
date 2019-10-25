package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.SlotGame
import com.onepiece.treasure.beans.value.database.SlotGameCo
import com.onepiece.treasure.beans.value.database.SlotGameUo

interface SlotGameService {

    fun findByPlatform(platform: Platform): List<SlotGame>

    fun create(slotGameCo: SlotGameCo)

    fun update(slotGameUo: SlotGameUo)

}
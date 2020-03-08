package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.SlotGame
import com.onepiece.gpgaming.beans.value.database.SlotGameValue

interface SlotGameService {

    fun findByPlatform(platform: Platform): List<SlotGame>

    fun create(slotGameCo: SlotGameValue.SlotGameCo)

    fun update(slotGameUo: SlotGameValue.SlotGameUo)

}
package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.SlotGame
import com.onepiece.gpgaming.beans.value.database.SlotGameCo
import com.onepiece.gpgaming.beans.value.database.SlotGameUo

interface SlotGameService {

    fun findByPlatform(platform: Platform): List<SlotGame>

    fun create(slotGameCo: SlotGameCo)

    fun update(slotGameUo: SlotGameUo)

}
package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.SlotGame
import com.onepiece.gpgaming.beans.value.database.SlotGameCo
import com.onepiece.gpgaming.beans.value.database.SlotGameUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface SlotGameDao: BasicDao<SlotGame> {

    fun findByPlatform(platform: Platform): List<SlotGame>

    fun create(slotGameCo: SlotGameCo): Boolean

    fun update(slotGameUo: SlotGameUo): Boolean

}
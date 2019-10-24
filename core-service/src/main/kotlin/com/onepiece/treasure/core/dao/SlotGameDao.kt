package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.SlotGameCo
import com.onepiece.treasure.core.dao.value.SlotGameUo
import com.onepiece.treasure.core.model.SlotGame

interface SlotGameDao: BasicDao<SlotGame> {

    fun create(slotGameCo: SlotGameCo): Boolean

    fun update(slotGameUo: SlotGameUo): Boolean

}
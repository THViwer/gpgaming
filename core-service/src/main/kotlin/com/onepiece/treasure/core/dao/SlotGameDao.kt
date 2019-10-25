package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.SlotGameCo
import com.onepiece.treasure.beans.value.database.SlotGameUo
import com.onepiece.treasure.beans.model.SlotGame

interface SlotGameDao: BasicDao<SlotGame> {

    fun create(slotGameCo: SlotGameCo): Boolean

    fun update(slotGameUo: SlotGameUo): Boolean

}
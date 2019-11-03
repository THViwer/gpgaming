package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.ArtificialOrder
import com.onepiece.treasure.beans.value.database.ArtificialOrderCo
import com.onepiece.treasure.beans.value.database.ArtificialOrderQuery
import com.onepiece.treasure.core.dao.basic.BasicDao

interface ArtificialOrderDao: BasicDao<ArtificialOrder> {

    fun query(query: ArtificialOrderQuery): List<ArtificialOrder>

    fun total(query: ArtificialOrderQuery): Int

    fun create(artificialOrder: ArtificialOrderCo): Boolean

}
package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.model.ArtificialOrder
import com.onepiece.treasure.beans.value.database.ArtificialOrderCo
import com.onepiece.treasure.beans.value.database.ArtificialOrderQuery

interface ArtificialOrderService {

    fun query(query: ArtificialOrderQuery): Page<ArtificialOrder>

    fun create(artificialOrderCo: ArtificialOrderCo)

}
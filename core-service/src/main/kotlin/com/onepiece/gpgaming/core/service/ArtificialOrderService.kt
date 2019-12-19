package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.model.ArtificialOrder
import com.onepiece.gpgaming.beans.value.database.ArtificialOrderCo
import com.onepiece.gpgaming.beans.value.database.ArtificialOrderQuery

interface ArtificialOrderService {

    fun query(query: ArtificialOrderQuery): Page<ArtificialOrder>

    fun create(artificialOrderCo: ArtificialOrderCo)

}